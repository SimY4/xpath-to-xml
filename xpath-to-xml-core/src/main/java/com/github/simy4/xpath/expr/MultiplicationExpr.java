package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class MultiplicationExpr extends AbstractOperationExpr {

    public MultiplicationExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    <N extends Node> View<N> resolve(Navigator<N> navigator, View<N> left, View<N> right, boolean greedy) {
        return new NumberView<>(left.toNumber() * right.toNumber());
    }

    @Override
    String operator() {
        return "*";
    }

}
