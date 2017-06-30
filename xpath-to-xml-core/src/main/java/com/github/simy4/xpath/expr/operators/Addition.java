package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

class Addition implements Operator {

    @Override
    public <N extends Node> View<N> resolve(ExprContext<N> context, View<N> left, View<N> right) {
        return new NumberView<>(left.toNumber() + right.toNumber());
    }

    @Override
    public String toString() {
        return "+";
    }

}
