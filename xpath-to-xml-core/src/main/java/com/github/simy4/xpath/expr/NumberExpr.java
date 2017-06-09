package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

public class NumberExpr implements Expr {

    private final Number number;

    public NumberExpr(Number number) {
        this.number = number;
    }

    @Override
    public <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml) {
        return Collections.<NodeWrapper<N>>singleton(new NodeWrapper.NumberNodeWrapper<N>(number));
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

}
