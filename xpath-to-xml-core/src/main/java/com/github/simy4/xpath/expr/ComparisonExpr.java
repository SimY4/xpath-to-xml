package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.op.Op;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Set;

public class ComparisonExpr extends AbstractExpr {

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
    public <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) {
        ExprContext<N> leftContext = context.clone(false, 1);
        Set<NodeView<N>> leftNodes = leftExpr.resolve(leftContext, xml);
        ExprContext<N> rightContext = context.clone(false, 1);
        Set<NodeView<N>> rightNodes = rightExpr.resolve(rightContext, xml);
        if (op.test(leftNodes, rightNodes)) {
            return Collections.singleton(xml);
        } else if (context.shouldCreate()) {
            leftContext = context.clone(1);
            leftNodes = leftExpr.resolve(leftContext, xml);
            rightContext = context.clone(1);
            rightNodes = rightExpr.resolve(rightContext, xml);
            op.apply(context.getNavigator(), leftNodes, rightNodes);
            return Collections.singleton(xml);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public String toString() {
        return leftExpr.toString() + op.toString() + rightExpr.toString();
    }

}
