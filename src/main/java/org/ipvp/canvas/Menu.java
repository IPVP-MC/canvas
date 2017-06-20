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
import org.bukkit.inventory.InventoryHolder;
import org.ipvp.canvas.slot.Slot;

/**
 * A menu represents an interactive interface for Players backed by instances of 
 * Inventories. 
 * <p>
 * Menu interaction will not function properly unless an instance of {@link MenuFunctionListener}
 * is properly registered with the Bukkit event scheduler.
 */
public interface Menu extends InventoryHolder, Iterable<Slot> {

    /**
     * Returns the fallback Menu for when this menu is closed
     * 
     * @return The parent menu
     */
    Optional<Menu> getParent();

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
     * Returns the Slot found at the given index of the Menu
     *
     * @param index The index of the Slot
     * @return The Slot at the index
     */
    Slot getSlot(int index);

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
     * A Builder for Menus
     */
    interface Builder {

        /**
         * Adds a title to this Menu
         *
         * @param title The title to display
         * @return Fluent pattern
         */
        Builder title(String title);

        /**
         * Adds a fallback parent to this Menu
         *
         * @param parent The fallback GUI
         * @return Fluent pattern
         */
        Builder parent(Menu parent);

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
    }
}
