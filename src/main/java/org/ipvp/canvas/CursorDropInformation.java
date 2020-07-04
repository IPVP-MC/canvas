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

import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Information about a cursor drop event performed by a player.
 */
public class CursorDropInformation {

    private final InventoryAction inventoryAction;
    private final ClickType clickType;
    private final Menu clickedMenu;
    private final ItemStack cursorItem;
    private Event.Result result;

    CursorDropInformation(InventoryAction inventoryAction, ClickType clickType, Menu clickedMenu, Event.Result result,
                          ItemStack cursorItem) {
        this.inventoryAction = inventoryAction;
        this.clickType = clickType;
        this.clickedMenu = clickedMenu;
        this.result = result;
        this.cursorItem = cursorItem;
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
     * Returns the result that the click will have in the inventory.
     *
     * <p>By default, this result will always be set to {@link org.bukkit.event.Event.Result#DENY}
     * and must be overriden using {@link #setResult(Event.Result)}.
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
     * Returns the item that is being dropped.
     *
     * @return dropped item
     */
    public ItemStack getCursorItem() {
        return cursorItem == null ? null : new ItemStack(cursorItem);
    }

    /**
     * @return the amount of items being added or removed.
     */
    public int getItemAmount() {
        switch (getAction()) {
            case DROP_ALL_CURSOR:
                return getCursorItem().getAmount();
            case DROP_ONE_CURSOR:
                return 1;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
