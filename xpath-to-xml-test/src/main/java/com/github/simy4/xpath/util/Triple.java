package com.github.simy4.xpath.util;

import com.google.errorprone.annotations.Immutable;

@Immutable(containerOf = {"F", "S", "T"})
public final class Triple<F, S, T> {

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
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
    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

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
