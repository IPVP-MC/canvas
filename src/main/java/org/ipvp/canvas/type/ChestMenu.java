package org.ipvp.canvas.type;

import org.ipvp.canvas.Menu;

/**
 * A menu that is backed by a chest inventory.
 */
public class ChestMenu extends AbstractMenu {

    ChestMenu(String title, int slots, Menu menu) {
        super(title, slots, menu);
    }

    /**
     * Returns a new builder.
     *
     * @param rows The amount of rows for the inventory to contain
     * @throws IllegalArgumentException if rows is not between 1 and 6 inclusive
     */
    public static Builder builder(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("invalid row count");
        }
        return new Builder(rows * 9);
    }

    @Override
    public Dimension getDimensions() {
        return new Dimension(getInventory().getSize() / 9, 9);
    }

    /**
     * A builder for creating a ChestMenu instance.
     */
    public static class Builder extends AbstractMenu.Builder {

        private int size;

        Builder(int size) {
            this.size = size;
        }

        @Override
        public ChestMenu build() {
            return new ChestMenu(getTitle(), size, getParent());
        }
    }
}
