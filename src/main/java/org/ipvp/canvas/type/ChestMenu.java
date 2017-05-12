package org.ipvp.canvas.type;

import org.ipvp.canvas.Menu;

public class ChestMenu extends AbstractMenu {
    
    ChestMenu(String title, int slots, Menu menu) {
        super(title, slots, menu);
    }

    public static Builder builder() {
        return builder(6);
    }

    public static Builder builder(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("invalid row count");
        }
        return new Builder(rows * 9);
    }

    static class Builder extends AbstractMenu.Builder {

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
