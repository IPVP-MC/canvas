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

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryAction;
import org.ipvp.canvas.slot.Slot;

/**
 * A menu represents an interactive interface for Players backed by instances of 
 * Inventories. 
 * <p>
 * Menu interaction will not function properly unless an instance of {@link MenuFunctionListener}
 * is properly registered with the Bukkit event scheduler.
 */
public interface Menu extends Iterable<Slot> {

    /**
     * Drop handler that allows all cursor items to be dropped by default.
     *
     * <p>Apply to a menu using {@link #setCursorDropHandler(CursorDropHandler)}.
     */
    CursorDropHandler ALLOW_CURSOR_DROPPING = (p, c) -> c.setResult(Event.Result.ALLOW);

    /**
     * Returns the fallback Menu for when this menu is closed
     * 
     * @return The parent menu
     */
    Optional<Menu> getParent();

    /**
     * Returns whether this menu will redraw.
     *
     * <p>If a player has an existing menu open and the
     * dimensions of the open inventory are the same as
     * this menu, then the contents of this menu will
     * be applied to the current inventory to preserve
     * cursor location.
     *
     * <p>Note: This feature cannot redraw the name of
     * inventories, so when a new inventory is open
     * and the contents are redrawn the name will not
     * change.
     *
     * @return redraw status
     */
    boolean isRedraw();

    /**
     * Returns whether the player currently has this menu open.
     *
     * @param viewer Player
     * @return true if the player has this menu open, false otherwise
     */
    boolean isOpen(Player viewer);

    /**
     * Opens the Menu for a Player
     *
     * @param viewer The player to view the Menu
     */
    void open(Player viewer);

    /**
     * Closes the Menu for a viewing Player
     *
     * @param viewer The player who currently is viewing this Menu
     * @throws IllegalStateException If the Player is not viewing the Menu
     */
    void close(Player viewer) throws IllegalStateException;

    /**
     * Re-renders the menu for the player.
     *
     * <p>If any items have changed in the inventory and a
     * {@link Slot} has a non-null item then any changes
     * for that slot will be overwritten.
     *
     * @param viewer player viewing inventory
     * @throws IllegalStateException If player is not viewing the menu
     */
    void update(Player viewer) throws IllegalStateException;

    /**
     * Returns the Slot found at the given index of the Menu.
     *
     * @param index The index of the Slot
     * @return The Slot at the index
     */
    Slot getSlot(int index);

    /**
     * Returns the Slot found at the given row and column of the Menu.
     *
     * <p>Rows and columns are not 0-indexed and both start at 1. For instance,
     * the very first slot of an inventory (slot 0, top left corner) is
     * the slot in the first row and first column. The last slot in
     * a double chest is the slot in the sixth row and ninth column.
     *
     * @param row menu row
     * @param column menu column
     * @return slot at coordinates
     */
    Slot getSlot(int row, int column);

    /**
     * Clears out the whole Menu
     */
    void clear();

    /**
     * Clears out a particular Slot at the given index
     *
     * @param index The index number to clear
     */
    void clear(int index);

    /**
     * Returns the dimensions of the Menu
     * 
     * @return The menus row and column count
     */
    Dimension getDimensions();

    /**
     * Returns a user-defined handler for when a Player closes the menu.
     *
     * @return The close handler
     */
    Optional<CloseHandler> getCloseHandler();

    /**
     * Sets a new handler policy for when a Player closes the menu.
     *
     * @param handler The new close handler
     */
    void setCloseHandler(CloseHandler handler);

    /**
     * Gets the cursor drop handler.
     *
     * @return drop handler
     */
    Optional<CursorDropHandler> getCursorDropHandler();

    /**
     * Sets a new handler policy for when players drop items from their cursor
     * outside the menu.
     *
     * @param handler drop handler
     */
    void setCursorDropHandler(CursorDropHandler handler);

    /**
     * A Menu close handler is a user defined function or policy that occurs when a
     * Player closes a menu.
     */
    @FunctionalInterface
    interface CloseHandler {

        /**
         * Called when a Player closes a menu, be it by navigating to a different 
         * inventory screen, or logging off.
         *
         * @param player The player that closed the menu
         * @param menu Menu that was closed
         */
        void close(Player player, Menu menu);
    }

    /**
     * Interface for handling events where a player drops an item using their
     * cursor outside the menu.
     *
     * <p>Since there is no slot clicked, this handler is triggered when an inventory
     * action with type {@link InventoryAction#DROP_ONE_CURSOR} or {@link InventoryAction#DROP_ALL_CURSOR}
     * is performed in an inventory.
     */
    @FunctionalInterface
    interface CursorDropHandler {

        /**
         * Called when a player drops an item with their cursor.
         *
         * @param player player dropping an item
         * @param click information about the performed click
         */
        void click(Player player, CursorDropInformation click);
    }

    /**
     * A Builder for Menus
     */
    interface Builder<T extends Builder<T>> {

        /**
         * Returns the dimensions of the Menu to be created.
         *
         * @return menu dimensions
         */
        Dimension getDimensions();

        /**
         * Adds a title to this Menu
         *
         * @param title The title to display
         * @return Fluent pattern
         */
        T title(String title);

        /**
         * Adds a fallback parent to this Menu
         *
         * @param parent The fallback GUI
         * @return Fluent pattern
         */
        T parent(Menu parent);

        /**
         * Sets the redraw flag of this Menu.
         *
         * @param redraw redraw flag
         * @return Fluent pattern
         * @see #isRedraw()
         */
        T redraw(boolean redraw);

        /**
         * Builds the Menu from the given data
         *
         * @return The instance of Menu, if successful
         */
        Menu build();
    }

    /**
     * Represents the dimensions of a Menu 
     */
    class Dimension {
        
        private final int rows;
        private final int columns;
        
        public Dimension(int rows, int columns) {
            this.rows = rows;
            this.columns = columns;
        }

        /**
         * Returns the number of rows in the Menu
         * 
         * @return The row count
         */
        public int getRows() {
            return rows;
        }

        /**
         * Returns the number of columns in the Menu
         * 
         * @return The column count
         */
        public int getColumns() {
            return columns;
        }

        /**
         * Returns the total area (slots) of the Menu
         *
         * @return The slot count
         */
        public int getArea() {
            return rows * columns;
        }

        @Override
        public int hashCode() {
            return rows * 31 + columns * 31;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            } else if (!(other instanceof Dimension)) {
                return false;
            }

            Dimension o = (Dimension) other;
            return o.rows == this.rows && o.columns == this.columns;
        }
    }
}
