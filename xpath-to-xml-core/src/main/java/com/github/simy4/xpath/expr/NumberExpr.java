package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;

import java.io.Serializable;

public class NumberExpr implements Expr, Serializable {

    private static final long serialVersionUID = 1L;

    private final NumberView<?> number;

    public NumberExpr(double number) {
        this.number = new NumberView<Node>(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> NumberView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy) {
        return (NumberView<N>) number;
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
