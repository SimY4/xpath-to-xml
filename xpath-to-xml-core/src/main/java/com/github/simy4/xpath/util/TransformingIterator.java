package com.github.simy4.xpath.util;

import java.util.Iterator;

public final class TransformingIterator<T, R> implements Iterator<R> {

    private final Iterator<? extends T> delegate;
    private final Function<? super T, ? extends R> transformation;

    public TransformingIterator(Iterator<? extends T> delegate, Function<? super T, ? extends R> transformation) {
        this.delegate = delegate;
        this.transformation = transformation;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public R next() {
        return transformation.apply(delegate.next());
    }

    @Override
    public void remove() {
        delegate.remove();
    }

}
