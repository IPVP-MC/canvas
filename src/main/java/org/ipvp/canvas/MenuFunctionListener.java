package org.ipvp.canvas;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.BoxMenu;

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
        if (top.getHolder() instanceof Menu) {
            Menu menu = (Menu) top.getHolder();
            ClickType clickType = event.getType() == DragType.EVEN ? ClickType.LEFT : ClickType.RIGHT;
            
            // Go through each slot affected and the item being inserted and pass the 
            // event through to the menu as a slot click
            Map<Integer, ItemStack> newItems = event.getNewItems();

            for (Map.Entry<Integer, ItemStack> entry : newItems.entrySet()) {
                int index = entry.getKey();
                ItemStack item = entry.getValue();
                if (index < top.getSize()) {
                    InventoryAction action = item.getAmount() > 1 ? InventoryAction.PLACE_SOME : InventoryAction.PLACE_ONE;
                    passClickToSlot(event, action, clickType, top, menu, index);
                    
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
        if (top.getHolder() instanceof Menu) {
            Menu menu = (Menu) top.getHolder();
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
                    // Fall through
                    
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
                    int firstMergeSlot = getNextAvailableSlot(top, moving); // Find the first item merging slot
                    
                    // An exit code of -1 indicates that no similar item was found and that
                    // the item will be moved into an empty slot.
                    if (firstMergeSlot == -1) {
                        firstMergeSlot = top.firstEmpty();
                        if (firstMergeSlot != -1) {
                            passClickToSlot(event, menu, firstMergeSlot);
                        }
                        return;
                    }
                    
                    // If there is a similar item, we need to check and process any item 
                    // overflowing in the inventory
                    passClickToSlot(event, menu, firstMergeSlot); // Pass the click to the first affected slot
                    
                    // Only proceed if the first slot didn't reject the event
                    if (event.getResult() != Event.Result.DENY) {
                        ItemStack target = top.getItem(firstMergeSlot); // Get the target item that we are merging into
                        int extraSpace = target.getMaxStackSize() - target.getAmount();
                        int overflow = moving.getAmount() - extraSpace;
                        
                        // If there is some overflow in the inventory we must find the next slot affected
                        // to pass the click to
                        if (overflow > 0) {
                            int nextSlot = getNextAvailableSlot(top, moving, firstMergeSlot); // Find next slot available
                            if (nextSlot == -1) {
                                nextSlot = top.firstEmpty();
                                if (nextSlot == -1) { // If it's still empty the inventory is full so we can exit
                                    return;
                                }
                            }
                            
                            passClickToSlot(event, menu, nextSlot); // Pass the click to the next affected slot
                        }
                    }
                    
                    break;
            }
        }
    }
    
    // Gets the next available slot that an item can be merged into
    private int getNextAvailableSlot(Inventory inventory, ItemStack moving) {
        return getNextAvailableSlot(inventory, moving, -1);
    }
    
    // Gets the next available slot that an item can be merged into, ignoring a specific slot
    private int getNextAvailableSlot(Inventory inventory, ItemStack moving, int ignoreSlot) {
        int targetSlot = 0;

        // We search the top inventory for the target slot/item that the item
        // clicked will be merged with. We retrieve the slot
        while (targetSlot < inventory.getSize()) {
            ItemStack inSlot = inventory.getItem(targetSlot);
            if (targetSlot != ignoreSlot && moving.isSimilar(inSlot) && inSlot.getAmount() < inSlot.getMaxStackSize()) {
                return targetSlot;
            }
            targetSlot++;
        }
        
        return -1;
    }
    
    // Passes an inventory click event to a menu at a given slot
    private void passClickToSlot(InventoryClickEvent event, Menu menu, int slotIndex) {
        passClickToSlot(event, event.getAction(), event.getClick(), event.getClickedInventory(), menu, slotIndex);
    }
    
    // Handles events where a slot was clicked inside an inventory
    private void passClickToSlot(InventoryInteractEvent handle, InventoryAction inventoryAction, ClickType clickType, 
                                 Inventory clicked, Menu menu, int slotIndex) {        
        // Fetch the slot that was clicked and process the information here
        Slot slot = menu.getSlot(slotIndex);
        ClickOptions options = slot.getClickOptions();
        
        // Check the options of the slot and set the result if the click is not allowed
        if (!options.isAllowedClickType(clickType) || !options.isAllowedAction(inventoryAction)) {
            handle.setResult(Event.Result.DENY);
        }
        
        ClickInformation clickInformation = new ClickInformation(handle, inventoryAction, clickType,
                clicked, menu, slot, handle.getResult());

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
        
        // If the player is shift clicking an item into a box menu we disallow it
        // because certain custom inventories shoot off a StackOverflowError when 
        // this event is allowed to process.
        if (top.getHolder() instanceof BoxMenu
                && top != event.getClickedInventory()
                && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }
}
