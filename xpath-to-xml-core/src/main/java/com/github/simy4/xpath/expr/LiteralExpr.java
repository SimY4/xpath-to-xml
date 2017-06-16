package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.View;

public class LiteralExpr implements Expr, Predicate {

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
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return !literal.isEmpty();
    }

    @Override
    public String toString() {
        return literal.toString();
    }

}
