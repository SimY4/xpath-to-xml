package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

class Multiplication implements Operator {

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right) {
        return new NumberView<N>(left.toNumber() * right.toNumber());
    }

    @Override
    public String toString() {
        return "*";
    }

}
