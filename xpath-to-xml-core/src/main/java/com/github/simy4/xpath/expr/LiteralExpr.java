package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

public class LiteralExpr extends AbstractExpr implements Expr {

    private final String literal;

    public LiteralExpr(String literal) {
        this.literal = literal;
    }

    @Override
    public <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml) {
        return Collections.<NodeWrapper<N>>singleton(new NodeWrapper.LiteralNodeWrapper<N>(literal));
    }

    @Override
    public String toString() {
        return "'" + literal + "'";
    }

}
