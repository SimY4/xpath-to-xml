package com.github.simy4.xpath.util;

import java.util.Iterator;

public abstract class ReadOnlyIterator<T> implements Iterator<T> {

    @Override
    public final void remove() {
        throw new UnsupportedOperationException("remove");
    }

}
