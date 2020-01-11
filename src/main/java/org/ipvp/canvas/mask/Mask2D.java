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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ipvp.canvas.Menu;

/**
 * @deprecated use {@link BinaryMask}
 */
@Deprecated
public class Mask2D implements Mask {

    private final Menu.Dimension dimension;
    private List<Integer> mask;

    Mask2D(Menu.Dimension dimension, List<Integer> mask) {
        this.dimension = dimension;
        this.mask = Collections.unmodifiableList(mask);
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

    }

    @Override
    public boolean test(int index) {
        return contains(index);
    }

    @Override
    public boolean test(int row, int col) {
        return test(row * 9 + col); // Differing logic from contains due to legacy compatibility
    }

    /**
     * @return All indices affected by this mask
     */
    public List<Integer> getMask() {
        return getSlots();
    }

    @Override
    public Iterator<Integer> iterator() {
        return mask.iterator();
    }

    /**
     * Returns a new builder that matches the dimensions of a Menu
     * 
     * @param menu The menu to build a mask for
     * @return A mask builder conforming to the dimensions of the Menu
     */
    public static Builder builder(Menu menu) {
        return builder(menu.getDimensions());
    }

    /**
     * Returns a new builder for specific dimensions
     *
     * @param dimensions menu dimensions
     * @return A mask builder for the specified number of rows and columns
     */
    public static Mask2D.Builder builder(Menu.Dimension dimensions) {
        return new Builder(dimensions);
    }

    /**
     * Returns a new builder for specific dimensions
     * 
     * @param rows The amount of rows to cover
     * @param cols The amount of columns to cover
     * @return A mask builder for the specified number of rows and columns
     */
    public static Mask2D.Builder builder(int rows, int cols) {
        return builder(new Menu.Dimension(rows, cols));
    }

    /**
     * A builder to create a Mask2D
     */
    public static class Builder implements Mask.Builder {

        private Menu.Dimension dimensions;
        private int currentLine;
        private int rows;
        private int cols;
        private int[][] mask;
        
        public Builder(Menu.Dimension dimensions) {
            this.dimensions = dimensions;
            this.rows = dimensions.getRows();
            this.cols = dimensions.getColumns();
            this.mask = new int[rows][cols];            
        }
        
        @Override
        public int currentLine() {
            return currentLine;
        }

        @Override
        public int rows() {
            return rows;
        }
        
        @Override
        public int columns() {
            return cols;
        }

        @Override
        public int row() {
            return currentLine;
        }

        @Override
        public Builder row(int row) throws IllegalStateException {
            if (row < 0 || row >= rows) {
                throw new IllegalStateException("row not between 0 and " + rows());
            }
            currentLine = row;
            return this;
        }

        @Override
        public Builder nextRow() throws IllegalStateException {
            if (currentLine == mask.length){
                throw new IllegalStateException("already at end");
            }
            ++currentLine;
            return this;
        }

        @Override
        public Builder previousRow() throws IllegalStateException {
            if (currentLine == 0) {
                throw new IllegalStateException("already at start");
            }
            --currentLine;
            return this;
        }

        @Override
        public Builder apply(String pattern) {
            char[] chars = pattern.toCharArray();
            for (int i = 0 ; i < 9 && i < chars.length ; i++) {
                String ch = String.valueOf(chars[i]);
                int c = Integer.parseInt(ch);
                mask[currentLine][i] = Math.min(c, 1);
            }
            return this;
        }

        @Override
        public Mask.Builder pattern(String pattern) {
            apply(pattern);
            if (row() != mask.length) {
                nextRow();
            }
            return this;
        }

        @Override
        public Mask2D build() {
            List<Integer> slots = new ArrayList<>();
            for (int r = 0; r < mask.length ; r++) {
                int[] col = mask[r];
                for (int c = 0 ; c < col.length ; c++) {
                    int state = col[c];
                    if (state == 1) {
                        slots.add(r * columns() + c);
                    }
                }
            }
            return new Mask2D(dimensions, slots);
        }
    }
}
