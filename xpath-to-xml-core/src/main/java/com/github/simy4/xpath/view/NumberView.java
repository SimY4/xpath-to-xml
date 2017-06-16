package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class NumberView<N> implements View<N> {

    private final Number number;

    public NumberView(Number number) {
        this.number = number;
    }

    @Override
    public int compareTo(View<N> other) {
        return Double.compare(number.doubleValue(), other.toNumber().doubleValue());
    }

    @Override
    public boolean toBoolean() {
        return 0 != Double.compare(0.0, number.doubleValue());
    }

    @Override
    public Number toNumber() {
        return number;
    }

    @Override
    public String toString() {
        return number.toString();
    }

    @Override
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
    }

    public Number getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NumberView<?> that = (NumberView<?>) o;

        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

}
