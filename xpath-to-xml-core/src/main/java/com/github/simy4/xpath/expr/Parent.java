package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

public class Parent extends AbstractStepExpr {

    public Parent(Iterable<? extends Predicate> predicates) {
        super(predicates);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseStep(ExprContext<N> context, NodeView<N> view) {
        final N parent = context.getNavigator().parentOf(view.getNode());
        return null == parent ? NodeSetView.<N>empty() : new NodeView<N>(parent);
    }

    @Override
    <N extends Node> N createStepNode(ExprContext<N> context, NodeView<N> parentView) throws XmlBuilderException {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
