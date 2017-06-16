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
    public int compareTo(View<N> other) {
        return other.visit(new NumberComparatorVisitor());
    }

    @Override
    public boolean isEmpty() {
        return 0 == Double.compare(0.0, number.doubleValue());
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

    @Override
    public String toString() {
        return number.toString();
    }

    private final class NumberComparatorVisitor implements ViewVisitor<N, Integer> {

        @Override
        public Integer visit(NodeSetView<N> nodeSet) {
            if (nodeSet.isEmpty()) {
                return 1;
            } else {
                return nodeSet.iterator().next().visit(this);
            }
        }

        @Override
        public Integer visit(LiteralView<N> literal) {
            try {
                return Double.compare(NumberView.this.number.doubleValue(), Double.parseDouble(literal.getLiteral()));
            } catch (NumberFormatException nfe) {
                return Double.compare(NumberView.this.number.doubleValue(), Double.NaN);
            }
        }

        @Override
        public Integer visit(NumberView<N> number) {
            return Double.compare(NumberView.this.number.doubleValue(), number.getNumber().doubleValue());
        }

        @Override
        public Integer visit(NodeView<N> node) {
            try {
                return Double.compare(NumberView.this.number.doubleValue(),
                        Double.parseDouble(node.getNode().getText()));
            } catch (NumberFormatException nfe) {
                return Double.compare(NumberView.this.number.doubleValue(), Double.NaN);
            }
        }

    }

}
