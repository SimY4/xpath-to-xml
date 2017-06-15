package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.View;

public class LiteralExpr implements Expr {

    private final LiteralView literal;

    public LiteralExpr(String literal) {
        this.literal = new LiteralView(literal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N> LiteralView<N> resolve(ExprContext<N> context, View<N> xml) {
        return (LiteralView<N>) literal;
    }

    @Override
    public String toString() {
        return literal.toString();
    }

}
