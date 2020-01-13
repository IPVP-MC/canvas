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

package org.ipvp.canvas.mask;

import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A mask that only considers values {@code 0} and {@code 1} in the
 * applied patterns.
 *
 * <p>Any character passed to the mask that is not explicitly {@code 1}
 * is considered a false value and as a result the slot will not be
 * contained by the resulting BinaryMask.
 */
public class BinaryMask implements Mask {

    private final Menu.Dimension dimension;
    private List<Integer> mask;
    private ItemStackTemplate item;

    BinaryMask(Menu.Dimension dimension, List<Integer> mask, ItemStackTemplate item) {
        this.dimension = dimension;
        this.mask = Collections.unmodifiableList(mask);
        this.item = item;
    }

    @Override
    public List<Integer> getSlots() {
        return mask;
    }

    @Override
    public Menu.Dimension getDimensions() {
        return dimension;
    }

    @Override
    public boolean contains(int index) {
        return mask.contains(index);
    }

    @Override
    public boolean contains(int row, int column) {
        int columns = getDimensions().getColumns();
        int firstRowIndex = (row - 1) * columns;
        int index = firstRowIndex + column - 1;
        return contains(index);
    }

    @Override
    public void apply(Menu menu) {
        for (int slot : getSlots()) {
            Slot affected = menu.getSlot(slot);
            affected.setItemTemplate(item);
        }
    }

    @Override
    public boolean test(int index) {
        return contains(index);
    }

    @Override
    public boolean test(int row, int col) {
        return contains(row, col);
    }

    @Override
    public Iterator<Integer> iterator() {
        return mask.iterator();
    }

    /**
     * Returns a new builder that matches the dimensions of a Menu
     *
     * @param menu target menu
     * @return mask builder for dimensions
     */
    public static BinaryMaskBuilder builder(Menu menu) {
        return builder(menu.getDimensions());
    }

    /**
     * Returns a new builder for specific dimensions.
     *
     * @param dimensions menu dimensions
     * @return mask builder for dimensions
     */
    public static BinaryMaskBuilder builder(Menu.Dimension dimensions) {
        return new BinaryMaskBuilder(dimensions);
    }

    /**
     * Returns a new builder for specific dimensions.
     *
     * @param rows row count of target menus
     * @param cols column count of target menus
     * @return mask builder for dimensions
     */
    public static BinaryMaskBuilder builder(int rows, int cols) {
        return builder(new Menu.Dimension(rows, cols));
    }

    /**
     * A builder to create a Mask2D
     */
    public static class BinaryMaskBuilder implements Mask.Builder {

        private Menu.Dimension dimensions;
        private int row;
        private int[][] mask;
        private ItemStackTemplate item;
        
        BinaryMaskBuilder(Menu.Dimension dimensions) {
            this.dimensions = dimensions;
            this.mask = new int[dimensions.getRows()][dimensions.getColumns()];
        }
        
        @Override
        public int currentLine() {
            return row;
        }

        @Override
        public int rows() {
            return dimensions.getRows();
        }
        
        @Override
        public int columns() {
            return dimensions.getColumns();
        }

        @Override
        public int row() {
            return row;
        }

        @Override
        public BinaryMaskBuilder row(int row) throws IllegalStateException {
            if (row < 1 || row > dimensions.getRows()) {
                throw new IllegalStateException("Row must be a value from 1 to " + rows());
            }
            this.row = row -1;
            return this;
        }

        /**
         * Sets the item that the mask will apply to an
         * inventory.
         *
         * @param item item
         * @return fluent pattern
         */
        public BinaryMaskBuilder item(ItemStack item) {
            return item(item == null ? null : new StaticItemTemplate(item));
        }

        /**
         * Sets the item that the mask will apply to an
         * inventory.
         *
         * @param item item
         * @return fluent pattern
         */
        public BinaryMaskBuilder item(ItemStackTemplate item) {
            this.item = item;
            return this;
        }

        @Override
        public BinaryMaskBuilder nextRow() throws IllegalStateException {
            if (row == mask.length){
                throw new IllegalStateException("Current line is the last row");
            }
            ++row;
            return this;
        }

        @Override
        public BinaryMaskBuilder previousRow() throws IllegalStateException {
            if (row == 0) {
                throw new IllegalStateException("Current line is the first row");
            }
            --row;
            return this;
        }

        @Override
        public BinaryMaskBuilder apply(String pattern) {
            char[] chars = pattern.toCharArray();
            for (int i = 0 ; i < dimensions.getColumns() && i < chars.length ; i++) {
                char c = chars[i];
                mask[row][i] = c == '1' ? 1 : 0;
            }
            return this;
        }

        @Override
        public BinaryMaskBuilder pattern(String pattern) {
            apply(pattern);
            if (row() != mask.length) {
                nextRow();
            }
            return this;
        }

        @Override
        public BinaryMask build() {
            List<Integer> slots = new ArrayList<>();
            for (int r = 0; r < dimensions.getRows() ; r++) {
                for (int c = 0 ; c < dimensions.getColumns() ; c++) {
                    int state = mask[r][c];
                    if (state == 1) {
                        slots.add(r * columns() + c);
                    }
                }
            }
            return new BinaryMask(dimensions, slots, item);
        }
    }
}
