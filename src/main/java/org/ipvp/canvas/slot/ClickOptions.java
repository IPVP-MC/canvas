package org.ipvp.canvas.slot;

import java.util.EnumSet;
import java.util.function.Predicate;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.ipvp.canvas.ClickInformation;

/**
 * Restrictions for when the execution of a Button will pass
 */
public class ClickOptions implements Predicate<ClickInformation> {

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

    @Override
    public boolean test(ClickInformation clickInformation) {
        return isAllowedAction(clickInformation.getAction())
                && isAllowedClickType(clickInformation.getClickType());
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
