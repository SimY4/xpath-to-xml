package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

@Immutable
public final class NumberView<N> implements View<N> {

    private final Number number;

    public NumberView(Number number) {
        this.number = number;
    }

    @Override
    public int compareTo(View<N> other) {
        final NumberComparatorVisitor<N> comparatorVisitor = new NumberComparatorVisitor<N>(number);
        other.visit(comparatorVisitor);
        return comparatorVisitor.getResult();
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

    @Override
    public String toString() {
        return number.toString();
    }

    @NotThreadSafe
    private static final class NumberComparatorVisitor<N> implements ViewVisitor<N> {

        private final Number number;
        private int result;

        private NumberComparatorVisitor(Number number) {
            this.number = number;
        }

        @Override
        public void visit(NodeSetView<N> nodeSet) {
            if (nodeSet.isEmpty()) {
                result = 1;
            } else {
                nodeSet.iterator().next().visit(this);
            }
        }

        @Override
        public void visit(LiteralView<N> literal) {
            try {
                result = Double.compare(number.doubleValue(), Double.parseDouble(literal.getLiteral()));
            } catch (NumberFormatException nfe) {
                result = Double.compare(number.doubleValue(), Double.NaN);
            }
        }

        @Override
        public void visit(NumberView<N> number) {
            result = Double.compare(this.number.doubleValue(), number.getNumber().doubleValue());
        }

        @Override
        public void visit(NodeView<N> node) {
            try {
                result = Double.compare(number.doubleValue(), Double.parseDouble(node.getNode().getText()));
            } catch (NumberFormatException nfe) {
                result = Double.compare(number.doubleValue(), Double.NaN);
            }
        }

        private int getResult() {
            return result;
        }

    }

}
