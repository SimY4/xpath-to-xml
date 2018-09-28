package com.github.simy4.xpath.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class TransformingAndFlatteningIterator<T, R> implements Iterator<R> {

    private final Iterator<? extends T> delegate;
    private final Function<? super T, ? extends Iterator<? extends R>> transformation;
    private Iterator<? extends R> current;

    public TransformingAndFlatteningIterator(Iterator<? extends T> delegate,
                                             Function<? super T, ? extends Iterator<? extends R>> transformation) {
        this(delegate, Collections.<R>emptyList().iterator(), transformation);
    }

    /**
     * Constructor.
     *
     * @param delegate       delegating iterator (tail generator)
     * @param initial        initial iterator (head)
     * @param transformation iterator transformation
     */
    public TransformingAndFlatteningIterator(Iterator<? extends T> delegate, Iterator<? extends R> initial,
                                             Function<? super T, ? extends Iterator<? extends R>> transformation) {
        this.delegate = delegate;
        this.current = initial;
        this.transformation = transformation;
    }

    @Override
    public boolean hasNext() {
        boolean currentHasNext;
        while (!(currentHasNext = current.hasNext()) && delegate.hasNext()) {
            current = transformation.apply(delegate.next());
        }
        return currentHasNext;
    }

    @Override
    public R next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return current.next();
    }

    @Override
    public void remove() {
        current.remove();
    }

}
