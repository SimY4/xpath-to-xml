package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
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
        return (NumberView<N>) number;
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        double number = this.number.toNumber();
        if (context.getPosition() == number) {
            return true;
        } else if (context.shouldCreate()) {
            if (xml instanceof NodeView) {
                createIndexedNode(context, (NodeView<N>) xml, number);
                return true;
            } else {
                throw new XmlBuilderException("Can not modify read-only node: " + xml);
            }
        } else {
            return false;
        }
    }

    private <N> void createIndexedNode(ExprContext<N> context, NodeView<N> xml, Number number) {
        final Node<N> node = xml.getNode();
        long numberOfNodesToCreate = number.longValue() - context.getPosition();
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
