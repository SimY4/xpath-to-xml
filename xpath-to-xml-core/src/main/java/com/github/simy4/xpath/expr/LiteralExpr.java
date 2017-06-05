package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;

public class LiteralExpr implements Expr {

    private final String literal;

    public LiteralExpr(String literal) {
        this.literal = literal;
    }

    public LiteralExpr(Number number) {
        this.literal = String.valueOf(number);
    }

    @Override
    public <N> List<NodeWrapper<N>> apply(Navigator<N> navigator, NodeWrapper<N> xml, boolean greedy) {
        return Collections.<NodeWrapper<N>>singletonList(new NodeWrapper.LiteralNodeWrapper<N>(literal));
    }

    @Override
    public String toString() {
        return "'" + literal + "'";
    }

}
