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

import org.ipvp.canvas.Menu;

import java.util.Collection;

/**
 * A basic inventory position mask.
 * <p>
 * Masks are helpful utility classes that abstract the requirement
 * to figure out which slot indices need to be modified in an inventory.
 * <p>
 * Calling {@link #iterator()} will return an iterator over all the 
 * available indices covered by this mask.
 */
public interface Mask extends Iterable<Integer> {

    /**
     * Returns the slot indexes affected by the mask.
     *
     * @return affected slots
     */
    Collection<Integer> getSlots();

    /**
     * Returns the dimensions that this mask is applicable for.
     *
     * <p>Note: Applying masks with non-matching dimensions
     * is possible however may not yield the expected results
     * if the column size is different.
     *
     * @return mask dimensions
     */
    Menu.Dimension getDimensions();

    /**
     * Returns whether the mask affects a certain index.
     *
     * @param index slot index
     * @return whether the mask affects the slot
     */
    boolean contains(int index);

    /**
     *
     * <p>Rows and columns are not 0-indexed and both start at 1. For instance,
     * the very first slot of an inventory (slot 0, top left corner) is
     * the slot in the first row and first column. The last slot in
     * a double chest is the slot in the sixth row and ninth column.
     *
     * @param row row position
     * @param column column position
     * @return whether the mask affects the slot
     */
    boolean contains(int row, int column);

    /**
     * Applies the mask to a menu.
     *
     * @param menu target menu
     */
    void apply(Menu menu);

    /**
     * @deprecated use {@link #contains(int)}
     */
    @Deprecated
    boolean test(int index);

    /**
     * @deprecated use {@link #contains(int, int)}
     */
    @Deprecated
    boolean test(int row, int col);

    /**
     * A Builder for Masks
     */
    interface Builder {

        /**
         * @return The current line that a pattern can be applied to
         * @deprecated use #row()
         */
        @Deprecated
        int currentLine();

        /**
         * @return The number of rows the pattern affects
         * @deprecated
         */
        @Deprecated
        int rows();

        /**
         * @return The number of columns the pattern affects
         * @deprecated
         */
        @Deprecated
        int columns();

        /**
         * Gets the current row a pattern will be applied to.
         *
         * @return row
         */
        int row();

        /**
         * Points the builder to a specific row to apply a mask to.
         * 
         * @param row The row number
         * @return Fluent pattern
         * @throws IllegalStateException If the row is not covered by the mask
         */
        Builder row(int row) throws IllegalStateException;
        
        /**
         * Increments the line counter to point to the next line
         * 
         * @return Fluent pattern
         * @throws IllegalStateException If the Builder is at the last line
         * @deprecated superseded by {@link #row(int)}
         */
        @Deprecated
        Builder nextRow();

        /**
         * Decrements the line counter to point to the previous line
         * 
         * @return Fluent pattern
         * @throws IllegalStateException If the Builder is at the first line
         * @deprecated superseded by {@link #row(int)}
         */
        @Deprecated
        Builder previousRow() throws IllegalStateException;

        /**
         * Applies a pattern to the line at the current index.
         *
         * <p>If the string provided is too long, the pattern will be cut
         * off at the last possible slot index in the row. Each character
         * of the pattern specifies a different function. Typically, a
         * character value of {@code '0'} defines that the mask will not
         * apply to the slot at the index, however this behavior is bound
         * to the implementation of the Builder.
         *
         * <p>Note: Unlike {@link #pattern(String)}, this method does
         * not skip to the next line and as such {@link #nextRow()} must
         * be called after every application.
         *
         * @param pattern A pattern to apply
         * @return Fluent pattern
         * @deprecated superseded by {@link #pattern(String)}
         */
        @Deprecated
        Builder apply(String pattern);

        /**
         * Applies a pattern to the line at the current index and moves
         * to the next line.
         * <p>
         * If the string provided is too long, the pattern will be cut
         * off at the last possible slot index in the row. Each character
         * of the pattern specifies a different function. Typically, a
         * character value of {@code '0'} defines that the mask will not
         * apply to the slot at the index, however this behavior is bound
         * to the implementation of the Builder.
         *
         * @param pattern A pattern to apply
         * @return Fluent pattern
         * @deprecated superseded by {@link #pattern(String)}
         */
        Builder pattern(String pattern);

        /**
         * Builds the Mask from the given data
         * 
         * @return The instance of Mask
         */
        Mask build();
    }
}