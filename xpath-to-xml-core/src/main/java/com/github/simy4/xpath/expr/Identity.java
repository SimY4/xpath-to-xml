package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Identity extends AbstractStepExpr {

    public Identity(List<Expr> predicateList) {
        super(predicateList);
    }

    @Override
    <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> parentNode) {
        return Collections.singleton(parentNode);
    }

    @Override
    <N> NodeWrapper<N> createStepNode(ExprContext<N> context) {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "." + super.toString();
    }

}
