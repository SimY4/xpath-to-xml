package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class SubtractionExpr extends AbstractOperationExpr {

    public SubtractionExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right) {
        return new NumberView<>(left.toNumber() - right.toNumber());
    }

    @Override
    String operator() {
        return "-";
    }

}
