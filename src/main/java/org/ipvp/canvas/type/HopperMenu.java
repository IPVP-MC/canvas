package org.ipvp.canvas.type;

import org.bukkit.event.inventory.InventoryType;
import org.ipvp.canvas.Menu;

/**
 * A menu backed by a Hopper Inventory.
 */
public class HopperMenu extends AbstractMenu {

    protected HopperMenu(String title, Menu menu) {
        super(title, InventoryType.HOPPER, menu);
    }

    /**
     * Returns a new builder. The Menu generated will be backed by a Hopper inventory.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Dimension getDimensions() {
        return new Dimension(1, 5);
    }

    /**
     * A builder for creating a BoxMenu instance.
     */
    public static class Builder extends AbstractMenu.Builder {

        @Override
        public HopperMenu build() {
            return new HopperMenu(getTitle(), getParent());
        }
    }
}
