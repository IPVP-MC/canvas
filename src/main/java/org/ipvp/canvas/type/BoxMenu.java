package org.ipvp.canvas.type;

import org.bukkit.event.inventory.InventoryType;
import org.ipvp.canvas.Menu;

/**
 * Represents a Menu backed by an instance of Inventory in the shape of a box.
 * <p>
 * Restricted to the following inventory types:
 * - WORKBENCH
 * - DISPENSER
 * - DROPPER
 */
public class BoxMenu extends AbstractMenu {

    BoxMenu(String title, InventoryType type, Menu menu) {
        super(title, type, menu);
    }

    /**
     * Returns a new builder. The Menu generated will be backed by an inventory with
     * a provided type
     *
     * @param type Type of inventory backing to generate
     * @throws IllegalArgumentException If the inventory type does not have 3x3 dimensions
     */
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

    @Override
    public Dimension getDimensions() {
        return new Dimension(3, 3);
    }

    /**
     * A builder for creating a BoxMenu instance.
     */
    public static class Builder extends AbstractMenu.Builder {

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
