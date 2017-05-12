package org.ipvp.canvas.type;

import org.bukkit.event.inventory.InventoryType;
import org.ipvp.canvas.Menu;

public class BoxMenu extends AbstractMenu {

    BoxMenu(String title, InventoryType type, Menu menu) {
        super(title, type, menu);
    }
    
    public static Builder builder() {
        return builder(InventoryType.WORKBENCH);
    }
    
    public static Builder builder(InventoryType type) {
        switch (type) {
            case WORKBENCH:
            case DISPENSER:
            case DROPPER:
                break;
            default:
                throw new IllegalArgumentException("box menu must have a 3x3 inventory type");
        }
        return new Builder(type);
    }
    
    static class Builder extends AbstractMenu.Builder {
        
        private InventoryType type;
        
        Builder(InventoryType type) {
            this.type = type;
        }
        
        @Override
        public BoxMenu build() {
            return new BoxMenu(getTitle(), type, getParent());
        }
    }
}
