package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.operators.Operator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class OperationExpr extends AbstractExpr {

    private final Expr leftExpr;
    private final Expr rightExpr;
    private final Operator operator;

    /**
     * Constructor.
     *
     * @param leftExpr  left expression
     * @param rightExpr right expression
     * @param operator comparison operator
     */
    public OperationExpr(Expr leftExpr, Expr rightExpr, Operator operator) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.operator = operator;
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context) {
        final View<N> leftView = leftExpr.resolve(context);
        final View<N> rightView = rightExpr.resolve(context);
        return operator.resolve(context, leftView, rightView);
    }

    @Override
    public String toString() {
        return leftExpr.toString() + operator.toString() + rightExpr.toString();
    }

}
