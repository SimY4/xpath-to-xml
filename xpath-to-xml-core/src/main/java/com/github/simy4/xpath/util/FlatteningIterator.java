package com.github.simy4.xpath.util;

import java.util.Collections;
import java.util.Iterator;

public final class FlatteningIterator<T> implements Iterator<T> {

    private final Iterator<? extends Iterator<? extends T>> metaIterator;
    private Iterator<? extends T> current;

    public FlatteningIterator(Iterator<? extends Iterator<? extends T>> iterator) {
        this(Collections.<T>emptyList().iterator(), iterator);
    }

    public FlatteningIterator(Iterator<? extends T> first, Iterator<? extends Iterator<? extends T>> rest) {
        this.current = first;
        this.metaIterator = rest;
    }

    @Override
    public boolean hasNext() {
        if (!current.hasNext()) {
            while (metaIterator.hasNext()) {
                current = metaIterator.next();
                if (current.hasNext()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public T next() {
        return current.next();
    }

    @Override
    public void remove() {
        current.remove();
    }

}
