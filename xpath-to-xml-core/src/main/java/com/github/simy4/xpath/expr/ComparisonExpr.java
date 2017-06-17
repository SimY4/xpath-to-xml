package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.operators.Operator;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.View;

public class ComparisonExpr implements Expr {

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
    public ComparisonExpr(Expr leftExpr, Expr rightExpr, Operator operator) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.operator = operator;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) {
        ExprContext<N> leftContext = context.clone(false, 1);
        View<N> leftView = leftExpr.resolve(leftContext, xml);
        ExprContext<N> rightContext = context.clone(false, 1);
        View<N> rightView = rightExpr.resolve(rightContext, xml);
        if (operator.test(leftView, rightView)) {
            return xml;
        } else if (context.shouldCreate()) {
            leftContext = context.clone(1);
            leftView = leftExpr.resolve(leftContext, xml);
            rightContext = context.clone(1);
            rightView = rightExpr.resolve(rightContext, xml);
            operator.apply(context.getNavigator(), leftView, rightView);
            return xml;
        } else {
            return NodeSetView.empty();
        }
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return resolve(context, xml).toBoolean();
    }

    @Override
    public String toString() {
        return leftExpr.toString() + operator.toString() + rightExpr.toString();
    }

}
