package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Parent extends AbstractStepExpr {

    public Parent(List<Expr> predicateList) {
        super(predicateList);
    }

    @Override
    <N> Set<NodeWrapper<N>> traverseStep(ExprContext<N> context, NodeWrapper<N> node) {
        NodeWrapper<N> parent = context.getNavigator().parentOf(node);
        return null == parent ? Collections.<NodeWrapper<N>>emptySet() : Collections.singleton(parent);
    }

    @Override
    <N> NodeWrapper<N> createStepNode(ExprContext<N> context) {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
