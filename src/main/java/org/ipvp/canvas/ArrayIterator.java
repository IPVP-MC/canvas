package org.ipvp.canvas;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T> {

    private T[] array;
    private int current;

    public ArrayIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return array.length > current;
    }

    @Override
    public T next() {
        return array[current++];
    }
}
