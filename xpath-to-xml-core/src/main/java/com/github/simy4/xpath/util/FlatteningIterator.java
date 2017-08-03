package com.github.simy4.xpath.util;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.Iterator;

@NotThreadSafe
public final class FlatteningIterator<T> implements Iterator<T> {

    private final Iterator<? extends Iterator<? extends T>> metaIterator;
    private Iterator<? extends T> current = Collections.<T>emptyList().iterator();

    public FlatteningIterator(Iterator<? extends Iterator<? extends T>> iterator) {
        this.metaIterator = iterator;
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
