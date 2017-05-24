package org.ipvp.canvas.mask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ipvp.canvas.Menu;

/**
 * 
 */
public class Mask2D implements Mask {

    private List<Integer> mask;

    Mask2D(List<Integer> mask) {
        this.mask = mask;
    }

    @Override
    public boolean test(int index) {
        return mask.contains(index);
    }

    @Override
    public boolean test(int row, int col) {
        return test(row * 9 + col);
    }

    /**
     * @return All indices affected by this mask
     */
    public List<Integer> getMask() {
        return mask;
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
        Menu.Dimension dimension = menu.getDimensions();
        return new Builder(dimension.getRows(), dimension.getColumns());
    }

    /**
     * Returns a new builder for specific dimensions
     * 
     * @param rows The amount of rows to cover
     * @param cols The amount of columns to cover
     * @return A mask builder for the specified number of rows and columns
     */
    public static Builder builder(int rows, int cols) {
        return new Builder(rows, cols);
    }

    /**
     * A builder to create a Mask2D
     */
    public static class Builder implements Mask.Builder {

        private int currentLine;
        private int rows;
        private int cols;
        private int[][] mask;
        
        public Builder(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
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
        public Builder nextLine() throws IllegalStateException {
            if (currentLine == mask.length){
                throw new IllegalStateException("already at end");
            }
            ++currentLine;
            return this;
        }

        @Override
        public Builder previousLine() throws IllegalStateException {
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
                int c = (int) chars[i];
                mask[currentLine][i] = Math.min(c, 1);
            }
            return this;
        }

        @Override
        public Mask2D build() {
            List<Integer> slots = new ArrayList<>();
            for (int r = 0; r < mask.length ; r++) {
                int[] col = mask[r];
                for (int c = 0 ; c < col.length ;c++) {
                    int state = col[c];
                    if (state == 1) {
                        slots.add(r * 9 + c);
                    }
                }
            }
            return new Mask2D(slots);
        }
    }
}
