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

import java.util.Objects;

import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.ipvp.canvas.slot.Slot;

/**
 * Information about a click performed by a player in a Menu
 */
public class ClickInformation {
    
    private final InventoryInteractEvent handle;
    private final InventoryAction inventoryAction;
    private final ClickType clickType;
    private final Inventory clicked;
    private final Menu clickedMenu;
    private final Slot clickedSlot;
    private Event.Result result;

    ClickInformation(InventoryInteractEvent handle, InventoryAction inventoryAction, ClickType clickType,
                     Inventory clicked, Menu clickedMenu, Slot clickedSlot, Event.Result result) {
        this.handle = handle;
        this.inventoryAction = inventoryAction;
        this.clickType = clickType;
        this.clicked = clicked;
        this.clickedMenu = clickedMenu;
        this.clickedSlot = clickedSlot;
        this.result = result;
    }

    /**
     * Returns the Menu that is handling the click
     * 
     * @return The handling Menu
     */
    public Menu getClickedMenu() {
        return clickedMenu;
    }

    /**
     * Returns the Slot that was clicked
     * 
     * @return The clicked Slot
     */
    public Slot getClickedSlot() {
        return clickedSlot;
    }

    /**
     * Returns the action performed by a player
     * 
     * @return The action of a player
     */
    public InventoryAction getAction() {
        return inventoryAction;
    }

    /**
     * Returns the type of click performed by a player
     * 
     * @return The type of click
     */
    public ClickType getClickType() {
        return clickType;
    }

    /**
     * Returns the result that the click will have in the inventory
     * 
     * @return The click result
     */
    public Event.Result getResult() {
        return result;
    }

    /**
     * Sets the result that the click will have in the inventory
     * 
     * @param result The new result
     */
    public void setResult(Event.Result result) {
        Objects.requireNonNull(result);
        this.result = result;
    }

    /**
     * @return Whether the Player is adding an item into the slot
     */
    public boolean isAddingItem() {
        switch (getAction()) {
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR:
            case HOTBAR_SWAP:
                return true;
            case MOVE_TO_OTHER_INVENTORY:
                return handle.getView().getBottomInventory() == clicked;
            default:
                return false;
        }
    }

    /**
     * @return Whether the Player is removing an item from the slot
     */
    public boolean isTakingItem() {
        switch (getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
            case PICKUP_ONE:
            case PICKUP_SOME:
            case SWAP_WITH_CURSOR:
                return true;
            case MOVE_TO_OTHER_INVENTORY:
            case HOTBAR_MOVE_AND_READD:
            case HOTBAR_SWAP:
                return handle.getView().getTopInventory() == clicked;
            default:
                return isDroppingItem();
        }
    }

    /**
     * @return Whether the Player is dropping the item in the slot
     */
    public boolean isDroppingItem() {
        switch (getAction()) {
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT:
                return true;
            default:
                return false;
        }
    }
}
