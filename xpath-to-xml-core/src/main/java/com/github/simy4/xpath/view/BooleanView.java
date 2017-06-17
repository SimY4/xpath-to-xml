package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class BooleanView<N> implements View<N> {

    private final boolean bool;

    public BooleanView(boolean bool) {
        this.bool = bool;
    }

    @Override
    public int compareTo(View<N> other) {
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
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BooleanView<?> that = (BooleanView<?>) o;

        return bool == that.bool;
    }

    @Override
    public int hashCode() {
        return (bool ? 1 : 0);
    }

}
