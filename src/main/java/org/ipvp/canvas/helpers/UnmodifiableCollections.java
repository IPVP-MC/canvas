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

package org.ipvp.canvas.helpers;

import com.google.common.collect.Iterators;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class UnmodifiableCollections<E> implements Collection<E> {


    private final Collection<? extends E>[] collections;

    public UnmodifiableCollections(Collection<? extends E>... c) {
        if (c==null) {
            throw new NullPointerException();
        }

        this.collections = c;
    }

    public int size() {
        int totalSize = 0;
        for(Collection<? extends E> c : collections) {
            totalSize += c.size();
        }
        return totalSize;
    }
    public boolean isEmpty() {
        for(Collection<? extends E> c : collections) {
            if(!c.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public boolean contains(Object o) {
        for(Collection<? extends E> c : collections) {
            if(c.contains(o)) {
                return true;
            }
        }
        return false;
    }
    public Object[] toArray() {
        Object[] array = new Object[size()];

        int lastIndex=0;
        for(Collection<? extends E> c : collections) {
            Object[] collectionArray = c.toArray();
            System.arraycopy(collectionArray, 0, array, lastIndex, collectionArray.length);
            lastIndex += collectionArray.length;
        }

        return array;
    }
    public <T> T[] toArray(T[] a) {
        T[] array = (T[]) new Object[size()];

        int lastIndex=0;
        for(Collection<? extends E> c : collections) {
            T[] collectionArray = c.toArray(a);
            System.arraycopy(collectionArray, 0, array, lastIndex, collectionArray.length);
            lastIndex += collectionArray.length;
        }

        return array;
    }


    public Iterator<E> iterator() {
        Iterator<? extends E> iterator = collections[0].iterator();
        for(int i=1; i+1<collections.length; i+=2) {
            iterator = Iterators.concat(iterator, Iterators.concat(collections[i].iterator(), collections[i+1].iterator()));
        }

        if(collections.length%2 == 0) {
            iterator = Iterators.concat(iterator, collections[collections.length - 1].iterator());
        }

        return (Iterator<E>) Iterators.unmodifiableIterator(iterator);
    }

    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> coll) {
        for(Collection<? extends E> c : collections) {
            if(c.containsAll(coll)) {
                return true;
            }
        }
        return false;
    }
    public boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    public void clear() {
        throw new UnsupportedOperationException();
    }

    // Override default methods in Collection
    @Override
    public void forEach(Consumer<? super E> action) {
        for(Collection<? extends E> c : collections) {
            c.forEach(action);
        }
    }
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }
    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
        //TODO Some logic to concat, possibly without making copies
        throw new UnsupportedOperationException("Unable to create a spliterator");
    }
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
        Stream<? extends E> stream = collections[0].stream();
        for(int i=1; i+1<collections.length; i+=2) {
            stream = Stream.concat(stream, Stream.concat(collections[i].stream(), collections[i+1].stream()));
        }

        if(collections.length%2 == 0) {
            stream = Stream.concat(stream, collections[collections.length - 1].stream());
        }

        return (Stream<E>) stream;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
        Stream<? extends E> stream = collections[0].parallelStream();
        for(int i=1; i+1<collections.length; i+=2) {
            stream = Stream.concat(stream, Stream.concat(collections[i].parallelStream(), collections[i+1].parallelStream()));
        }

        if(collections.length%2 == 0) {
            stream = Stream.concat(stream, collections[collections.length - 1].parallelStream());
        }

        return (Stream<E>) stream;
    }

}
