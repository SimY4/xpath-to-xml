package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

abstract class AbstractOperationExpr implements Expr {

    private final Expr leftExpr;
    private final Expr rightExpr;

    AbstractOperationExpr(Expr leftExpr, Expr rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    @Override
    public final <N extends Node> View<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        final var leftView = leftExpr.resolve(context);
        final var rightView = rightExpr.resolve(context);
        return resolve(context, leftView, rightView);
    }

    abstract <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException;

    abstract String operator();

    @Override
    public final String toString() {
        return leftExpr.toString() + operator() + rightExpr.toString();
    }

}
