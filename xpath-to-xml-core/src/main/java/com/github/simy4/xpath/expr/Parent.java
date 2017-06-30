package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.util.List;

public class Parent extends AbstractStepExpr {

    public Parent(List<Predicate> predicateList) {
        super(predicateList);
    }

    @Override
    <N extends Node> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> view) {
        final N parent = context.getNavigator().parentOf(view.getNode());
        return null == parent ? NodeSetView.empty() : NodeSetView.singleton(new NodeView<>(parent));
    }

    @Override
    <N extends Node> NodeView<N> createStepNode(ExprContext<N> context, NodeView<N> parentView)
            throws XmlBuilderException {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
