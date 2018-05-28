package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Parent extends AbstractStepExpr {

    public Parent(Iterable<Expr> predicates) {
        super(predicates);
    }

    @Override
    <N extends Node> IterableNodeView<N> resolveStep(ViewContext<N> context) throws XmlBuilderException {
        final N parent = context.getNavigator().parentOf(context.getCurrent().getNode());
        return null == parent ? NodeSetView.empty() : new NodeView<>(parent);
    }

    @Override
    <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
