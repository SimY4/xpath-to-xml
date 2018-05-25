package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Parent extends AbstractStepExpr {

    public Parent(Iterable<Predicate<ViewContext<?>>> predicates) {
        super(predicates);
    }

    @Override
    <N extends Node> IterableNodeView<N> traverseStep(Navigator<N> navigator, NodeView<N> view) {
        final N parent = navigator.parentOf(view.getNode());
        return null == parent ? NodeSetView.<N>empty() : new NodeView<N>(parent);
    }

    @Override
    public <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
