package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.LiteralView;

public class LiteralExpr extends AbstractExpr {

    private final LiteralView literal;

    public LiteralExpr(String literal) {
        this.literal = new LiteralView(literal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> LiteralView<N> resolve(ExprContext<N> context) {
        return (LiteralView<N>) literal;
    }

    @Override
    public String toString() {
        return "'" + literal.toString() + "'";
    }

}
