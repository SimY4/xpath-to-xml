package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.google.errorprone.annotations.Immutable;

@Immutable
public final class LiteralView<N extends Node> implements View<N> {

    private final String literal;

    public LiteralView(String literal) {
        this.literal = literal;
    }

    @Override
    public int compareTo(View<N> other) {
        return literal.compareTo(other.toString());
    }

    @Override
    public boolean toBoolean() {
        return !literal.isEmpty();
    }

    @Override
    public double toNumber() {
        try {
            return Double.parseDouble(literal);
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        }
    }

    @Override
    public String toString() {
        return literal;
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return ((this == o || null != o) && o instanceof View) && literal.equals(o.toString());
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }

}
