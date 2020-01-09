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

package org.ipvp.canvas.slot;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.ClickInformation;
import org.ipvp.canvas.template.ItemStackTemplate;

/**
 * A slot is a position held by an Inventory that can be interacted with by users.
 * <p>
 * A Menu slot always has the potential to be clicked by players, controlling
 * what interactions have any effect on the slot are made available through {@link ClickOptions}.
 */
public interface Slot {

    /**
     * Returns the slots index position inside the parent Menu.
     *
     * @return The slots position
     */
    int getIndex();

    /**
     * Returns the click interactions that the Slot permits.
     *
     * @return The slots options
     */
    ClickOptions getClickOptions();

    /**
     * Modifies the expected function of the slot when handling clicks.
     *
     * @param options The new slot options
     */
    void setClickOptions(ClickOptions options);

    /**
     * Returns the item currently present in the slot. Return value
     * has undefined behavior when the stack is of type AIR.
     *
     * @return The item in the slot
     * @deprecated superseded by {@link #getItem(Player)}
     */
    @Deprecated
    ItemStack getItem();

    /**
     * Returns the item currently present in the slot, rendered
     * for a specific player.
     *
     * @param viewer player viewing the menu/slot
     * @return item rendered for the player
     */
    ItemStack getItem(Player viewer);

    /**
     * Sets the item currently in the slot.
     *
     * @param item The new item in the slot
     */
    void setItem(ItemStack item);

    /**
     * Sets the item template to render in the slot.
     *
     * @param item New item template
     */
    void setItemTemplate(ItemStackTemplate item);

    /**
     * Returns a user-defined handler for when a Player clicks the slot.
     *
     * @return The click handler
     */
    Optional<ClickHandler> getClickHandler();

    /**
     * Sets a new handler policy for when a Player clicks the slot.
     *
     * @param handler The new click handler
     */
    void setClickHandler(ClickHandler handler);

    /**
     * Get the settings of the slot.
     *
     * @return slot settings
     */
    SlotSettings getSettings();

    /**
     * Set the settings of the slot.
     *
     * @param settings slot settings
     */
    void setSettings(SlotSettings settings);

    /**
     * A Slots click handler is a user defined function or policy that occurs when a
     * Player clicks on a slot.
     * <p>
     * Passed through are the player that clicked the slot as well as the information
     * regarding their click, including the clicked Menu as well as the slot clicked.
     */
    @FunctionalInterface
    interface ClickHandler {

        /**
         * Called when a Player successfully clicks on a slot with this handler. Users may
         * change the result a click will have in the underlying inventory by setting the
         * information in the passed through ClickInformation object.
         *
         * @param player The player that clicked the slot
         * @param click  Information about the performed click
         */
        void click(Player player, ClickInformation click);
    }
}
