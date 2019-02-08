package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;

public class NumberExpr implements Expr {

    private final NumberView<?> number;

    public NumberExpr(double number) {
        this.number = new NumberView<>(number);
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
