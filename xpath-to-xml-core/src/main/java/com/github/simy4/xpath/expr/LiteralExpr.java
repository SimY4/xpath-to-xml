package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Set;

public class LiteralExpr extends AbstractExpr {

    private final String literal;

    public LiteralExpr(String literal) {
        this.literal = literal;
    }

    @Override
    public <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) {
        return Collections.<NodeView<N>>singleton(new NodeView.LiteralNodeView<N>(literal));
    }

    @Override
    public String toString() {
        return "'" + literal + "'";
    }

}
