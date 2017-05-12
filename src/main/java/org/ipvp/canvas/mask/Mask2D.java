package org.ipvp.canvas.mask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mask2D implements Mask {

    private List<Integer> mask;

    private Mask2D(List<Integer> mask) {
        this.mask = mask;
    }

    @Override
    public boolean test(int slot) {
        return mask.contains(slot);
    }

    @Override
    public boolean test(int row, int col) {
        return test(row * 9 + col);
    }

    public List<Integer> getMask() {
        return mask;
    }

    @Override
    public Iterator<Integer> iterator() {
        return mask.iterator();
    }

    public static Builder builder() {
        return builder(1);
    }

    public static Builder builder(int lines) {
        return new Builder(lines);
    }

    private static class Builder implements Mask.Builder {

        private int currentLine;
        private int lines;
        private int[][] mask;
        
        public Builder(int lines) {
            this.lines = lines;
            this.mask = new int[lines][9];            
        }
        
        @Override
        public int currentLine() {
            return currentLine;
        }

        @Override
        public int maxLines() {
            return lines;
        }

        @Override
        public Builder nextLine() {
            if (currentLine == mask.length){
                throw new IllegalStateException("already at end");
            }
            ++currentLine;
            return this;
        }

        @Override
        public Builder previousLine() {
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
        public Mask build() {
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
