package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Set;

public class NumberExpr extends AbstractExpr {

    private final Number number;

    public NumberExpr(Number number) {
        this.number = number;
    }

    @Override
    public <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) {
        return Collections.<NodeView<N>>singleton(new NodeView.NumberNodeView<N>(number));
    }

    @Override
    public <N> boolean apply(ExprContext<N> context, NodeView<N> xml) throws XmlBuilderException {
        if (number.doubleValue() != number.longValue()) {
            return true;
        } else if (context.getPosition() == number.longValue()) {
            return true;
        } else if (context.shouldCreate()) {
            long numberOfNodesToCreate = number.longValue() - context.getPosition();
            NodeView<N> lastNode;
            do {
                lastNode = context.getNavigator().clone(xml);
                context.getNavigator().prepend(xml, lastNode);
            } while (--numberOfNodesToCreate > 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

}
