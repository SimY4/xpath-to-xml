package com.github.simy4.xpath.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class FilteringIterator<T> implements Iterator<T> {

    private final Iterator<? extends T> iterator;
    private final Predicate<? super T> predicate;
    private T nextElement;
    private boolean hasNext;

    /**
     * Constructor.
     *
     * @param iterator  delegating iterator
     * @param predicate predicate to apply
     */
    public FilteringIterator(Iterator<? extends T> iterator, Predicate<? super T> predicate) {
        this.iterator = iterator;
        this.predicate = predicate;
        nextMatch();
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public T next() {
        if (!hasNext) {
            throw new NoSuchElementException("No more elements");
        }
        return nextMatch();
    }

    private T nextMatch() {
        final var oldMatch = nextElement;
        while (iterator.hasNext()) {
            final var next = iterator.next();
            if (predicate.test(next)) {
                hasNext = true;
                nextElement = next;
                return oldMatch;
            }
        }
        hasNext = false;
        return oldMatch;
    }

}
