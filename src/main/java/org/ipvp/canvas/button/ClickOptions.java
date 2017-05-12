package org.ipvp.canvas.button;

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
     * Click options that allow any InventoryAction and ClickType
     */
    public static ClickOptions ALLOW_ALL = ClickOptions.builder().allActions().allClickTypes().build();

    private EnumSet<InventoryAction> allowedActions;
    private EnumSet<ClickType> allowedClickTypes;
    
    private ClickOptions() {
        
    }
    
    public boolean isAllowedAction(InventoryAction action) {
        return allowedActions.contains(action);
    }
    
    public boolean isAllowedClickType(ClickType clickType) {
        return allowedClickTypes.contains(clickType);
    }
    
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean test(ClickInformation clickInformation) {
        return isAllowedAction(clickInformation.getAction())
                && isAllowedClickType(clickInformation.getClickType());
    }

    public static class Builder {

        private EnumSet<InventoryAction> allowedActions = EnumSet.noneOf(InventoryAction.class);
        private EnumSet<ClickType> allowedClickTypes = EnumSet.noneOf(ClickType.class);
        
        private Builder() {
            
        }
        
        public Builder allActions() {
            this.allowedActions = EnumSet.allOf(InventoryAction.class);
            return this;
        }
        
        public Builder allClickTypes() {
            this.allowedClickTypes = EnumSet.allOf(ClickType.class);
            return this;
        }
        
        public Builder allow(InventoryAction action) {
            allowedActions.add(action);
            return this;
        }
        
        public Builder allow(InventoryAction... actions) {
            for (InventoryAction action : actions) {
                allow(action);
            }
            return this;
        }
        
        public Builder allow(ClickType clickType) {
            allowedClickTypes.add(clickType);
            return this;
        }
        
        public Builder allow(ClickType... clickTypes) {
            for (ClickType type : clickTypes) {
                allow(type);
            }
            return this;
        }
        
        public ClickOptions build() {
            ClickOptions options = new ClickOptions();
            options.allowedActions = allowedActions;
            options.allowedClickTypes = allowedClickTypes;
            return options;
        }
    }
    
}
