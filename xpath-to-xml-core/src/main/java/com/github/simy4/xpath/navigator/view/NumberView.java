package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class NumberView<N> implements View<N> {

    private final Number number;

    public NumberView(Number number) {
        this.number = number;
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
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
