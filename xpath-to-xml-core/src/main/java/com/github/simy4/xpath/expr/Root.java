package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

public class Root implements StepExpr {

    @Override
    public <N> Set<NodeWrapper<N>> apply(ExprContext<N> context, NodeWrapper<N> xml, boolean greedy)
            throws XmlBuilderException {
        Set<NodeWrapper<N>> result = traverse(context, xml);
        if (result.isEmpty() && greedy) {
            result = Collections.singleton(createNode(context));
        }
        return result;
    }

    @Override
    public <N> Set<NodeWrapper<N>> traverse(ExprContext<N> context, NodeWrapper<N> parentNode) {
        return Collections.singleton(context.getNavigator().root());
    }

    @Override
    public <N> NodeWrapper<N> createNode(ExprContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Root node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "/";
    }

}
