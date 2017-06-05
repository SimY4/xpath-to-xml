package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;

public class NumberExpr implements Expr {

    private final Number number;

    public NumberExpr(Number number) {
        this.number = number;
    }

    @Override
    public <N> List<NodeWrapper<N>> apply(Navigator<N> navigator, NodeWrapper<N> xml, boolean greedy) {
        return Collections.<NodeWrapper<N>>singletonList(new NodeWrapper.NumberNodeWrapper<N>(number));
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

}
