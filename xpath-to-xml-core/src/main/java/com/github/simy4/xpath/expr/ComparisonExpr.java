package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

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
    public <N> Set<NodeWrapper<N>> apply(ExprContext<N> context, NodeWrapper<N> xml, boolean greedy) {
        final ExprContext<N> comparisonContext = new ExprContext<N>(context.getNavigator(), 1, 1);
        final Set<NodeWrapper<N>> leftNodes = leftExpr.apply(comparisonContext, xml, greedy);
        final Set<NodeWrapper<N>> rightNodes = rightExpr.apply(comparisonContext, xml, false);
        if (op.test(leftNodes, rightNodes)) {
            return Collections.singleton(xml);
        } else if (greedy) {
            if (!rightNodes.isEmpty()) {
                final NodeWrapper<N> rightNode = rightNodes.iterator().next();
                for (NodeWrapper<N> leftNode : leftNodes) {
                    context.getNavigator().setText(leftNode, rightNode.getText());
                }
            }
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
