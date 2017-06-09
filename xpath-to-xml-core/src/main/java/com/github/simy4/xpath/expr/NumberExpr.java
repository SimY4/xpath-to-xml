package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

public class NumberExpr implements Expr, Predicate {

    private final Number number;

    public NumberExpr(Number number) {
        this.number = number;
    }

    @Override
    public Predicate asPredicate() {
        if (number.doubleValue() == number.longValue()) {
            return this;
        } else {
            return new Predicate() {
                @Override
                public <N> boolean apply(ExprContext<N> context, NodeWrapper<N> xml) {
                    Set<NodeWrapper<N>> result = NumberExpr.this.resolve(context, xml);
                    return !result.isEmpty();
                }
            };
        }
    }

    @Override
    public <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml) {
        return Collections.<NodeWrapper<N>>singleton(new NodeWrapper.NumberNodeWrapper<N>(number));
    }

    @Override
    public <N> boolean apply(ExprContext<N> context, NodeWrapper<N> xml) throws XmlBuilderException {
        if (context.getPosition() == number.longValue()) {
            return true;
        } else if (context.shouldCreate()) {
            long numberOfNodesToCreate = number.longValue() - context.getPosition();
            NodeWrapper<N> lastNode;
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
