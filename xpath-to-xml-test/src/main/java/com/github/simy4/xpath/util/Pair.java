package com.github.simy4.xpath.util;

public final class Pair<F, S> {

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) {
            return false;
        }
        return second != null ? second.equals(pair.second) : pair.second == null;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return first + " -> " + second;
    }

}
