package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.util.List;

public class Identity extends AbstractStepExpr {

    public Identity(List<Predicate> predicateList) {
        super(predicateList);
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> xml) {
        return NodeSetView.singleton(xml);
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
