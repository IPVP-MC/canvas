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
import org.bukkit.inventory.ShapedRecipe;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.template.ItemStackTemplate;
import org.ipvp.canvas.template.StaticItemTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A mask that accepts maps items to specific characters,
 * similar to how a {@link ShapedRecipe} may work.
 *
 * <p>When building a RecipeMask any items added are mapped to
 * a specific character that is applied to the mask. Upon
 * application of the mask on a menu, the item for the
 * slots character is applied.
 *
 * <p>For example, if a mask such as {@code ["rrrbbbggg"]}
 * is created for a single row menu, and the following items
 * are added via the builder:
 * <ul>
 *     <li>for character 'r', a RED_STAINED_GLASS item</li>
 *     <li>for character 'g', a GREEN_STAINED_GLASS item</li>
 *     <li>for character 'b', a BLUE_STAINED_GLASS item</li>
 * </ul>
 * Then when the RecipeMask is applied to a menu, the row
 * that is written to will have 3 red stained glass items
 * followed by 3 green stained glass items followed by 3 blue
 * stained glass items.
 */
public class RecipeMask implements Mask {

    private final Menu.Dimension dimension;
    private Map<Integer, Character> mask;
    private Map<Character, ItemStackTemplate> items;

    RecipeMask(Menu.Dimension dimension, Map<Integer, Character> mask, Map<Character, ItemStackTemplate> items) {
        this.dimension = dimension;
        this.mask = Collections.unmodifiableMap(mask);
        this.items = Collections.unmodifiableMap(items);
    }

    @Override
    public Collection<Integer> getSlots() {
        return mask.keySet();
    }

    @Override
    public Menu.Dimension getDimensions() {
        return dimension;
    }

    @Override
    public boolean contains(int index) {
        return mask.containsKey(index);
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
        mask.forEach((slot, character) -> {
            ItemStackTemplate item = items.get(character);
            Slot affected = menu.getSlot(slot);
            affected.setItemTemplate(item);
        });
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
        return mask.keySet().iterator();
    }

    /**
     * Returns a new builder that matches the dimensions of a Menu
     *
     * @param menu target menu
     * @return mask builder for dimensions
     */
    public static RecipeMaskBuilder builder(Menu menu) {
        return builder(menu.getDimensions());
    }

    /**
     * Returns a new builder for specific dimensions.
     *
     * @param dimensions menu dimensions
     * @return mask builder for dimensions
     */
    public static RecipeMaskBuilder builder(Menu.Dimension dimensions) {
        return new RecipeMaskBuilder(dimensions);
    }

    /**
     * Returns a new builder for specific dimensions.
     *
     * @param rows row count of target menus
     * @param cols column count of target menus
     * @return mask builder for dimensions
     */
    public static RecipeMaskBuilder builder(int rows, int cols) {
        return builder(new Menu.Dimension(rows, cols));
    }

    /**
     * A builder to create a Mask2D
     */
    public static class RecipeMaskBuilder implements Builder {

        private Menu.Dimension dimensions;
        private int row;
        private char[][] mask;
        private Map<Character, ItemStackTemplate> items = new HashMap<>();

        RecipeMaskBuilder(Menu.Dimension dimensions) {
            this.dimensions = dimensions;
            this.mask = new char[dimensions.getRows()][dimensions.getColumns()];
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
        public RecipeMaskBuilder row(int row) throws IllegalStateException {
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
        public RecipeMaskBuilder item(char character, ItemStack item) {
            return item(character, item == null ? null : new StaticItemTemplate(item));
        }

        /**
         * Sets the item that the mask will apply to an
         * inventory.
         *
         * @param item item
         * @return fluent pattern
         */
        public RecipeMaskBuilder item(char character, ItemStackTemplate item) {
            if (item == null) {
                items.remove(character);
            } else {
                items.put(character, item);
            }
            return this;
        }

        @Override
        public RecipeMaskBuilder nextRow() throws IllegalStateException {
            if (row == mask.length){
                throw new IllegalStateException("Current line is the last row");
            }
            ++row;
            return this;
        }

        @Override
        public RecipeMaskBuilder previousRow() throws IllegalStateException {
            if (row == 0) {
                throw new IllegalStateException("Current line is the first row");
            }
            --row;
            return this;
        }

        @Override
        public RecipeMaskBuilder apply(String pattern) {
            char[] chars = pattern.toCharArray();
            for (int i = 0 ; i < dimensions.getColumns() && i < chars.length ; i++) {
                mask[row][i] = chars[i];
            }
            return this;
        }

        @Override
        public RecipeMaskBuilder pattern(String pattern) {
            apply(pattern);
            if (row() != mask.length) {
                nextRow();
            }
            return this;
        }

        @Override
        public RecipeMask build() {
            Map<Integer, Character> slots = new HashMap<>();
            for (int r = 0; r < dimensions.getRows() ; r++) {
                for (int c = 0 ; c < dimensions.getColumns() ; c++) {
                    char character = mask[r][c];
                    slots.put(r * columns() + c, character);
                }
            }
            return new RecipeMask(dimensions, slots, items);
        }
    }
}
