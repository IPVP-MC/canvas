/*
 * Copyright (C) Matthew Steglinski (SainttX) <matt@ipvp.org>
 * Copyright (C) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.ipvp.canvas;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.AbstractMenu;
import org.ipvp.canvas.type.MenuHolder;

/**
 * A listener that maintains the required functions of Menus.
 * <p>
 * Effectively handles all required events and passes through necessary data
 * to Menu instances that are being interacted with by players.
 */
public final class MenuFunctionListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handleGuiDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        
        // We are only processing drags that affect menus
        if (top.getHolder() instanceof MenuHolder) {
            Menu menu = ((MenuHolder) top.getHolder()).getMenu();
            ClickType clickType = event.getType() == DragType.EVEN ? ClickType.LEFT : ClickType.RIGHT;
            
            // Go through each slot affected and the item being inserted and pass the 
            // event through to the menu as a slot click
            Map<Integer, ItemStack> newItems = event.getNewItems();

            for (Map.Entry<Integer, ItemStack> entry : newItems.entrySet()) {
                int index = entry.getKey();
                ItemStack item = entry.getValue();
                if (index < top.getSize()) {
                    InventoryAction action = item.getAmount() > 1 ? InventoryAction.PLACE_SOME : InventoryAction.PLACE_ONE;
                    passClickToSlot(event, action, clickType, top, menu, index, event.getNewItems().get(index));

                    // If the event has been denied by any slot we simply exit out as 
                    // nothing else should be processed
                    if (event.getResult() == Event.Result.DENY) {
                        return;
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void handleGuiClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();

        // We are only processing clicks taking place in the view of a menu
        if (top.getHolder() instanceof MenuHolder) {
            Menu menu = ((MenuHolder) top.getHolder()).getMenu();
            Inventory clicked = event.getClickedInventory();
            InventoryAction action = event.getAction();
            
            // Need to find the target slot
            switch (action) {
                // Simply exit if we don't know what happened. This typically occurs when 
                // the player clicks the area outside the inventory screen.
                case UNKNOWN:
                case NOTHING:
                    break;
                
                // We handle all events where the player is attempting to drop an item from the 
                // cursor. Since there is no slot that is being clicked, there is no way to 
                // relay the information to the menu, so we simply cancel the event to stay on 
                // the safe side.
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    event.setResult(Event.Result.DENY);
                    if (menu.getCursorDropHandler().isPresent()) {
                        CursorDropInformation dropInformation = new CursorDropInformation(action, event.getClick(),
                                menu, event.getResult(), event.getCursor());
                        menu.getCursorDropHandler().get().click((Player) event.getWhoClicked(), dropInformation);
                        event.setResult(dropInformation.getResult());
                    } else {
                        event.setCancelled(true);
                    }
                    break;
                    
                // We also disallow collecting to cursor as this has a more complicated behavior
                // than we are willing to process. 
                case COLLECT_TO_CURSOR:
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                
                // Cases where the item might be being taken from the Menu
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                case PICKUP_ALL:
                case PICKUP_HALF:
                case PICKUP_ONE:
                case PICKUP_SOME:
                case HOTBAR_MOVE_AND_READD:
                    // Fall through
                
                // Cases where the item might be being inserted into a Slot in the Menu
                case PLACE_ALL:
                case PLACE_ONE:
                case PLACE_SOME:
                    // Fall through

                // Mix cases where the player might be both picking up and setting down items
                // in the slot in the same action
                case HOTBAR_SWAP:
                case SWAP_WITH_CURSOR:
                    // First we need to verify that the player is clicking inside the menu
                    // and not on the bottom
                    if (clicked == top) {
                        // Send the information to the slot
                        passClickToSlot(event, menu, event.getSlot());
                    }
                    break;
                
                // This is a special case that we process since the direction of the item 
                // can go both ways, and if it is an insertion we we must find the slot(s) 
                // that are being affected and pass information to all of them. 
                case MOVE_TO_OTHER_INVENTORY:
                    // If the clicked inventory is the top inventory we have an easy job
                    // that is identical to the handling in the other click types.
                    if (clicked == top) {
                        passClickToSlot(event, menu, event.getSlot());
                        break;
                    }
                    
                    ItemStack moving = event.getCurrentItem();
                    int amountLeft = moving.getAmount();
                    int nextAvailableSlot = getNextAvailableSlot(top, moving);
                    
                    // We iterate over the next available merge slots while the item is being merged (amount > 0)
                    // to pass the event to all of the slots that will be effected. Once the event is denied,
                    // the item is fully taken care of by all slots, or the inventory runs out of space we are
                    // safe to break out of the loop.
                    while (nextAvailableSlot > -1 && amountLeft > 0 && event.getResult() != Event.Result.DENY) {
                        ItemStack inSlot = top.getItem(nextAvailableSlot);

                        int maxAvailable; // Track how much we will be adding to the stack

                        if (inSlot == null || inSlot.getType() == Material.AIR) {
                            maxAvailable = moving.getMaxStackSize();
                        } else {
                            maxAvailable = inSlot.getMaxStackSize() - inSlot.getAmount();
                        }

                        maxAvailable = Math.min(maxAvailable, amountLeft);
                        amountLeft -= maxAvailable;

                        ItemStack adding = new ItemStack(moving);
                        adding.setAmount(maxAvailable);
                        passClickToSlot(event, event.getAction(), event.getClick(), event.getClickedInventory(), menu, nextAvailableSlot, adding);
                        nextAvailableSlot = getNextAvailableSlot(top, moving, nextAvailableSlot + 1);
                    }
                    break;
            }
        }
    }
    
    // Gets the next available slot that an item can be merged into
    private int getNextAvailableSlot(Inventory inventory, ItemStack moving) {
        return getNextAvailableSlot(inventory, moving, 0);
    }
    
    // Gets the next available slot that an item can be merged into, after a specific slot
    private int getNextAvailableSlot(Inventory inventory, ItemStack moving, int startPosition) {
        // Start at the starting position and iterate through every slot in the inventory. We search
        // the inventory for the target slot that is related to the item being moved.
        for (int targetSlot = startPosition ; targetSlot < inventory.getSize() ; targetSlot++) {
            ItemStack inSlot = inventory.getItem(targetSlot);
            if (moving.isSimilar(inSlot) && inSlot.getAmount() < inSlot.getMaxStackSize()) {
                return targetSlot;
            }
        }
        
        // Default return value of -1 to indicate no similar slot found.
        return inventory.firstEmpty();
    }
    
    // Passes an inventory click event to a menu at a given slot
    private void passClickToSlot(InventoryClickEvent event, Menu menu, int slotIndex) {
        passClickToSlot(event, event.getAction(), event.getClick(), event.getClickedInventory(), menu, slotIndex);
    }
    
    // Handles events where a slot was clicked inside an inventory
    private void passClickToSlot(InventoryInteractEvent handle, InventoryAction inventoryAction, ClickType clickType, 
                                 Inventory clicked, Menu menu, int slotIndex) {
        passClickToSlot(handle, inventoryAction, clickType, clicked, menu, slotIndex, null);
    }

    // Handles events where a slot was clicked inside an inventory
    private void passClickToSlot(InventoryInteractEvent handle, InventoryAction inventoryAction, ClickType clickType,
                                 Inventory clicked, Menu menu, int slotIndex, ItemStack addingItem) {
        // Fetch the slot that was clicked and process the information here
        Slot slot = menu.getSlot(slotIndex);
        ClickOptions options = slot.getClickOptions();

        // Check the options of the slot and set the result if the click is not allowed
        if (!options.isAllowedClickType(clickType) || !options.isAllowedAction(inventoryAction)) {
            handle.setResult(Event.Result.DENY);
        }

        ClickInformation clickInformation = new ClickInformation(handle, inventoryAction, clickType,
                clicked, menu, slot, handle.getResult(), addingItem);

        // Process the click information for the event if the slot has a click handler
        if (slot.getClickHandler().isPresent()) {
            slot.getClickHandler().get().click((Player) handle.getWhoClicked(), clickInformation);
        }

        // Complete the handling of the event by setting the result of the click
        handle.setResult(clickInformation.getResult());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void preventShiftClickInCustomTiles(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        
        // If the player is shift clicking an item into a disallowed inventory type 
        // we disallow it because certain custom inventories shoot off a 
        // StackOverflowError when this event is allowed to process.
        if (top.getHolder() instanceof MenuHolder && isShiftClickingBlocked(top.getType())
                && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }
    
    // Returns true if shift clicking into a specific inventory type is allowed
    private boolean isShiftClickingBlocked(InventoryType type) {
        switch (type) {
            case HOPPER:
            case WORKBENCH:
            case DROPPER:
            case DISPENSER:
                return true;
            default:
                return false;
        }
    }
    
    @EventHandler
    public void triggerCloseHandler(InventoryCloseEvent event) {
        Inventory closed = event.getInventory();

        // If the player is closing a menu that has a close handler,
        // we trigger the handler for functions to run
        if (closed.getHolder() instanceof MenuHolder) {
            Menu menu = ((MenuHolder) closed.getHolder()).getMenu();
            ((AbstractMenu) menu).closedByPlayer((Player) event.getPlayer(), true);
        }
    }
}
