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

import java.util.EnumSet;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

/**
 * Restrictions for when the execution of a Button will pass
 */
public class ClickOptions {

    /**
     * Click options that allow all actions and click types.
     */
    public static ClickOptions ALLOW_ALL = ClickOptions.builder().allActions().allClickTypes().build();

    /**
     * Click options that deny all actions and click types. 
     */
    public static ClickOptions DENY_ALL = ClickOptions.builder().build();

    private EnumSet<InventoryAction> allowedActions;
    private EnumSet<ClickType> allowedClickTypes;
    
    private ClickOptions() {
        
    }

    /**
     * Returns whether an inventory action can be performed on the button.
     * 
     * @param action An action performed by a player
     * @return True if the action is allowed, false otherwise
     */
    public boolean isAllowedAction(InventoryAction action) {
        return allowedActions.contains(action);
    }

    /**
     * Returns whether a click type can be performed on the button.
     *
     * @param clickType The type of click that was performed by a player
     * @return True if the action is allowed, false otherwise
     */
    public boolean isAllowedClickType(ClickType clickType) {
        return allowedClickTypes.contains(clickType);
    }

    /**
     * Returns a new builder. The initial builder state will effectively
     * be the same as {@link #DENY_ALL}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder used for specifying what click types are allowed for Buttons
     */
    public static class Builder {

        private EnumSet<InventoryAction> allowedActions = EnumSet.noneOf(InventoryAction.class);
        private EnumSet<ClickType> allowedClickTypes = EnumSet.noneOf(ClickType.class);
        
        private Builder() {
            
        }

        /**
         * Allows any inventory action to be performed on a Button
         * 
         * @return Fluent pattern
         */
        public Builder allActions() {
            this.allowedActions = EnumSet.allOf(InventoryAction.class);
            return this;
        }

        /**
         * Allows any click type to be performed on a Button
         * 
         * @return Fluent pattern
         */
        public Builder allClickTypes() {
            this.allowedClickTypes = EnumSet.allOf(ClickType.class);
            return this;
        }

        /**
         * Allows a specific Inventory action be performed on a Button
         * 
         * @param action Action to allow
         * @return Fluent pattern
         */
        public Builder allow(InventoryAction action) {
            allowedActions.add(action);
            return this;
        }

        /**
         * Allows specific Inventory actions be performed on a Button
         * 
         * @param actions Actions to allow
         * @return Fluent pattern
         */
        public Builder allow(InventoryAction... actions) {
            for (InventoryAction action : actions) {
                allow(action);
            }
            return this;
        }

        /**
         * Allows a specific click type be performed on a Button
         * @param clickType Click type to allow
         *                  
         * @return Fluent pattern
         */
        public Builder allow(ClickType clickType) {
            allowedClickTypes.add(clickType);
            return this;
        }

        /**
         * Allows specific click types be performed on a Button
         * @param clickTypes Click types to allow
         *
         * @return Fluent pattern
         */
        public Builder allow(ClickType... clickTypes) {
            for (ClickType type : clickTypes) {
                allow(type);
            }
            return this;
        }

        /**
         * Builds the options from the provided data
         * 
         * @return The instance of ClickOptions
         */
        public ClickOptions build() {
            ClickOptions options = new ClickOptions();
            options.allowedActions = allowedActions;
            options.allowedClickTypes = allowedClickTypes;
            return options;
        }
    }
    
}
