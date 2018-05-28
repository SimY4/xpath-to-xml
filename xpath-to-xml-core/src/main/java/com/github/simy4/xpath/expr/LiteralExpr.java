package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.ViewContext;

public class LiteralExpr implements Expr {

    private final LiteralView<?> literal;

    public LiteralExpr(String literal) {
        this.literal = new LiteralView(literal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> LiteralView<N> resolve(ViewContext<N> context) {
        return (LiteralView<N>) literal;
    }

    @Override
    public String toString() {
        return "'" + literal.toString() + "'";
    }

}
