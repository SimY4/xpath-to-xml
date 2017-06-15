package com.github.simy4.xpath.utils;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Triple<F, S, T> {

    public static <A, B, C> Triple<A, B, C> of(@Nullable A first, @Nullable B second, @Nullable C third) {
        return new Triple<A, B, C>(first, second, third);
    }

    private final F first;
    private final S second;
    private final T third;

    /**
     * Constructor.
     *
     * @param first  first argument
     * @param second second argument
     * @param third  third argument
     */
    public Triple(@Nullable F first, @Nullable S second, @Nullable T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Nullable
    public F getFirst() {
        return first;
    }

    @Nullable
    public S getSecond() {
        return second;
    }

    @Nullable
    public T getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;

        if (first != null ? !first.equals(triple.first) : triple.first != null) {
            return false;
        }
        if (second != null ? !second.equals(triple.second) : triple.second != null) {
            return false;
        }
        return third != null ? third.equals(triple.third) : triple.third == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ')';
    }

}
