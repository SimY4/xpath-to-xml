package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Identity extends AbstractStepExpr {

    public Identity(List<Predicate> predicateList) {
        super(predicateList);
    }

    @Override
    <N> Set<NodeView<N>> traverseStep(ExprContext<N> context, NodeView<N> parentNode) {
        return Collections.singleton(parentNode);
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> context) {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "." + super.toString();
    }

}
