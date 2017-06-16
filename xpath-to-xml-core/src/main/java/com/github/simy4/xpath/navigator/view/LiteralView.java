package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

@Immutable
public final class LiteralView<N> implements View<N> {

    private final String literal;

    public LiteralView(String literal) {
        this.literal = literal;
    }

    @Override
    public int compareTo(View<N> other) {
        final LiteralComparatorVisitor<N> comparatorVisitor = new LiteralComparatorVisitor<N>(literal);
        other.visit(comparatorVisitor);
        return comparatorVisitor.getResult();
    }

    @Override
    public void visit(ViewVisitor<N> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LiteralView<?> that = (LiteralView<?>) o;

        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return literal.hashCode();
    }

    @Override
    public String toString() {
        return "'" + literal + "'";
    }

    @NotThreadSafe
    private static final class LiteralComparatorVisitor<N> implements ViewVisitor<N> {

        private final String literal;
        private int result;

        private LiteralComparatorVisitor(String literal) {
            this.literal = literal;
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
            result = this.literal.compareTo(literal.getLiteral());
        }

        @Override
        public void visit(NumberView<N> number) {
            result = literal.compareTo(number.getNumber().toString());
        }

        @Override
        public void visit(NodeView<N> node) {
            result = literal.compareTo(node.getNode().getText());
        }

        private int getResult() {
            return result;
        }

    }

}
