package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class LiteralView<N> implements View<N> {

    private final String literal;

    public LiteralView(String literal) {
        this.literal = literal;
    }

    @Override
    public int compareTo(View<N> other) {
        return other.visit(new LiteralComparatorVisitor());
    }

    @Override
    public boolean isEmpty() {
        return literal.isEmpty();
    }

    @Override
    public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
        return visitor.visit(this);
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

    private final class LiteralComparatorVisitor implements ViewVisitor<N, Integer> {

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
            return LiteralView.this.literal.compareTo(literal.getLiteral());
        }

        @Override
        public Integer visit(NumberView<N> number) {
            return LiteralView.this.literal.compareTo(number.getNumber().toString());
        }

        @Override
        public Integer visit(NodeView<N> node) {
            return LiteralView.this.literal.compareTo(node.getNode().getText());
        }

    }

}
