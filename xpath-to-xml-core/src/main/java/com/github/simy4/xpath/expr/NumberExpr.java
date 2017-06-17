package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class NumberExpr implements Expr {

    private final NumberView number;

    public NumberExpr(double number) {
        this.number = new NumberView(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N> NumberView<N> resolve(ExprContext<N> context, View<N> xml) {
        double number = this.number.toNumber();
        if (context.shouldCreate() && xml instanceof NodeView && number % 1 == 0 && context.getPosition() < number) {
            createIndexedNode(context, (NodeView<N>) xml, number);
        }
        return this.number;
    }

    private <N> void createIndexedNode(ExprContext<N> context, NodeView<N> xml, double number) {
        final Node<N> node = xml.getNode();
        long numberOfNodesToCreate = (long) number - context.getPosition();
        Node<N> lastNode;
        do {
            lastNode = context.getNavigator().clone(node);
            context.getNavigator().prepend(node, lastNode);
        } while (--numberOfNodesToCreate > 0);
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
