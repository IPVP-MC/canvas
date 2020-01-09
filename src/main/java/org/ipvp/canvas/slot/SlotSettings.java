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

import org.bukkit.entity.Player;
import org.ipvp.canvas.template.ItemStackTemplate;

/**
 * Utility class that holds the settings of a slot.
 */
public class SlotSettings {

    private ClickOptions clickOptions;
    private Slot.ClickHandler clickHandler;
    private ItemStackTemplate itemTemplate;

    SlotSettings(ClickOptions clickOptions, Slot.ClickHandler clickHandler, ItemStackTemplate itemTemplate) {
        this.clickOptions = clickOptions;
        this.clickHandler = clickHandler;
        this.itemTemplate = itemTemplate;
    }

    /**
     * @see Slot#getClickOptions()
     */
    public ClickOptions getClickOptions() {
        return clickOptions;
    }

    /**
     * @see Slot#getClickHandler()
     */
    public Slot.ClickHandler getClickHandler() {
        return clickHandler;
    }

    /**
     * @see Slot#getItem(Player)
     */
    public ItemStackTemplate getItemTemplate() {
        return itemTemplate;
    }

    /**
     * Returns a new builder.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating slot settings.
     */
    public static class Builder {

        private ClickOptions clickOptions;
        private Slot.ClickHandler clickHandler;
        private ItemStackTemplate itemTemplate;

        private Builder() {

        }

        /**
         * @see Slot#setClickOptions(ClickOptions)
         */
        public Builder clickOptions(ClickOptions clickOptions) {
            this.clickOptions = clickOptions;
            return this;
        }

        /**
         * @see Slot#setClickHandler(Slot.ClickHandler)
         */
        public Builder clickHandler(Slot.ClickHandler clickHandler) {
            this.clickHandler = clickHandler;
            return this;
        }

        /**
         * @see Slot#setItemTemplate(ItemStackTemplate)
         */
        public Builder itemTemplate(ItemStackTemplate itemTemplate) {
            this.itemTemplate = itemTemplate;
            return this;
        }

        /**
         * Builds a new slot details instance.
         *
         * @return slot details
         */
        public SlotSettings build() {
            return new SlotSettings(clickOptions, clickHandler, itemTemplate);
        }
    }
}
