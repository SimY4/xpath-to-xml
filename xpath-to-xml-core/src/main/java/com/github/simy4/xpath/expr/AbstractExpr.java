package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Set;

abstract class AbstractExpr implements Expr, Predicate {

    @Override
    public final Predicate asPredicate() {
        return this;
    }

    @Override
    public <N> boolean apply(ExprContext<N> context, NodeView<N> xml) throws XmlBuilderException {
        final Set<NodeView<N>> result = resolve(context, xml);
        return !result.isEmpty();
    }

}
