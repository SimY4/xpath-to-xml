package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Parent extends AbstractStepExpr {

    public Parent(List<Predicate> predicateList) {
        super(predicateList);
    }

    @Override
    <N> Set<NodeView<N>> traverseStep(ExprContext<N> context, NodeView<N> node) {
        NodeView<N> parent = context.getNavigator().parentOf(node);
        return null == parent ? Collections.<NodeView<N>>emptySet() : Collections.singleton(parent);
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> context) {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
