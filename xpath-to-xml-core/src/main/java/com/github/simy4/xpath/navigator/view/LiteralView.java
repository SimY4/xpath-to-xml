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

}
