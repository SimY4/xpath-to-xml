package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Set;

abstract class AbstractExpr implements Expr, Predicate {

    @Override
    public Predicate asPredicate() {
        return this;
    }

    @Override
    public <N> boolean apply(ExprContext<N> context, NodeWrapper<N> xml) throws XmlBuilderException {
        final Set<NodeWrapper<N>> result = resolve(context, xml);
        return !result.isEmpty();
    }

}
