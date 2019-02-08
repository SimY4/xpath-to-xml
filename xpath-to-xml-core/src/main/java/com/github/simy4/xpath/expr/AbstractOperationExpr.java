package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

abstract class AbstractOperationExpr implements Expr {

    private final Expr leftExpr;
    private final Expr rightExpr;

    AbstractOperationExpr(Expr leftExpr, Expr rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public final <N extends Node> View<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        final var newGreedy = !view.hasNext() && greedy;
        final var leftView = leftExpr.resolve(navigator, view, newGreedy);
        final var rightView = rightExpr.resolve(navigator, view, newGreedy);
        return resolve(navigator, leftView, rightView, newGreedy);
    }

    abstract <N extends Node> View<N> resolve(Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
            throws XmlBuilderException;

    abstract String operator();

    @Override
    public final String toString() {
        return leftExpr.toString() + operator() + rightExpr.toString();
    }

}
