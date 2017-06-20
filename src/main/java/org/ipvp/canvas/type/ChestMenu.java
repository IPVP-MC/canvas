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

package org.ipvp.canvas.type;

import org.ipvp.canvas.Menu;

/**
 * A menu that is backed by a chest inventory.
 */
public class ChestMenu extends AbstractMenu {

    protected ChestMenu(String title, int slots, Menu menu) {
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
