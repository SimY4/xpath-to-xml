package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;

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
    public <N> List<NodeWrapper<N>> apply(Navigator<N> navigator, NodeWrapper<N> xml, boolean greedy) {
        final List<NodeWrapper<N>> leftNodes = leftExpr.apply(navigator, xml, greedy);
        final List<NodeWrapper<N>> rightNodes = rightExpr.apply(navigator, xml, false);
        if (op.test(leftNodes, rightNodes)) {
            return Collections.singletonList(xml);
        } else if (greedy) {
            if (!rightNodes.isEmpty()) {
                final NodeWrapper<N> rightNode = rightNodes.get(0);
                for (NodeWrapper<N> leftNode : leftNodes) {
                    navigator.setText(leftNode, rightNode.getText());
                }
            }
            return Collections.singletonList(xml);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return leftExpr.toString() + op.toString() + rightExpr.toString();
    }

}
