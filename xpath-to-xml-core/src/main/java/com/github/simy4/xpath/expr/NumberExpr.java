package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;

public class NumberExpr implements Expr {

    private final NumberView number;

    public NumberExpr(Number number) {
        this.number = new NumberView(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N> NumberView<N> resolve(ExprContext<N> context, View<N> xml) {
        return (NumberView<N>) number;
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        Number number = this.number.getNumber();
        if (number.doubleValue() != number.longValue()) {
            return this.number.isEmpty();
        } else if (context.getPosition() == number.longValue()) {
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
        return this.number.toString();
    }

}
