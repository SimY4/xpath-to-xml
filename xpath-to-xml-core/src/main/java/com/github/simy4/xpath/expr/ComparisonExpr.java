package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.op.Op;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.View;

public class ComparisonExpr implements Expr {

    private final Expr leftExpr;
    private final Expr rightExpr;
    private final Op op;

    /**
     * Constructor.
     *
     * @param leftExpr  left expression
     * @param rightExpr right expression
     * @param op        comparison operation
     */
    public ComparisonExpr(Expr leftExpr, Expr rightExpr, Op op) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
        this.op = op;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) {
        ExprContext<N> leftContext = context.clone(false, 1);
        View<N> leftView = leftExpr.resolve(leftContext, xml);
        ExprContext<N> rightContext = context.clone(false, 1);
        View<N> rightView = rightExpr.resolve(rightContext, xml);
        if (op.test(leftView, rightView)) {
            return xml;
        } else if (context.shouldCreate()) {
            leftContext = context.clone(1);
            leftView = leftExpr.resolve(leftContext, xml);
            rightContext = context.clone(1);
            rightView = rightExpr.resolve(rightContext, xml);
            op.apply(context.getNavigator(), leftView, rightView);
            return xml;
        } else {
            return NodeSetView.empty();
        }
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return !resolve(context, xml).isEmpty();
    }

    @Override
    public String toString() {
        return leftExpr.toString() + op.toString() + rightExpr.toString();
    }

}
