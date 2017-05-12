package org.ipvp.canvas.mask;

public interface Mask extends Iterable<Integer> {
    
    boolean test(int slot);
    
    boolean test(int row, int col);

    interface Builder {
        
        int currentLine();
        
        int maxLines();

        Builder nextLine();

        Builder previousLine();

        Builder apply(String pattern);
        
        Mask build();
    }
}
