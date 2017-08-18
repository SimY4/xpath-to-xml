package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class NumberView<N extends Node> implements View<N> {

    private final double number;

    public NumberView(double number) {
        this.number = number;
    }

    @Override
    public int compareTo(@Nonnull View<N> other) {
        return Double.compare(number, other.toNumber());
    }

    @Override
    public boolean toBoolean() {
        return 0 != Double.compare(0.0, number);
    }

    @Override
    public double toNumber() {
        return number;
    }

    @Override
    public String toString() {
        return Double.toString(number);
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

}
