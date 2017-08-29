package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class BooleanView<N extends Node> implements View<N> {

    private static final BooleanView FALSE = new BooleanView(false);
    private static final BooleanView TRUE = new BooleanView(true);

    @SuppressWarnings("unchecked")
    public static <T extends Node> BooleanView<T> of(boolean bool) {
        return (BooleanView<T>) (bool ? TRUE : FALSE);
    }

    private final boolean bool;

    private BooleanView(boolean bool) {
        this.bool = bool;
    }

    @Override
    public int compareTo(@Nonnull View<N> other) {
        final boolean thatBool = other.toBoolean();
        return (bool == thatBool) ? 0 : (bool ? 1 : -1);
    }

    @Override
    public boolean toBoolean() {
        return bool;
    }

    @Override
    public double toNumber() {
        return bool ? 1.0 : 0.0;
    }

    @Override
    public String toString() {
        return Boolean.toString(bool);
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || null != o && o instanceof View && bool == ((View<?>) o).toBoolean();
    }

    @Override
    public int hashCode() {
        return (bool ? 1 : 0);
    }

}
