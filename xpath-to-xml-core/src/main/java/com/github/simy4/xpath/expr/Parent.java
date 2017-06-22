package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.util.List;

public class Parent extends AbstractStepExpr {

    public Parent(List<Expr> predicateList) {
        super(predicateList);
    }

    @Override
    <N> NodeSetView<N> traverseStep(ExprContext<N> context, NodeView<N> view) {
        final Node<N> parent = context.getNavigator().parentOf(view.getNode());
        return null == parent ? NodeSetView.<N>empty() : NodeSetView.singleton(new NodeView<N>(parent));
    }

    @Override
    <N> NodeView<N> createStepNode(ExprContext<N> context, NodeView<N> parentView) throws XmlBuilderException {
        throw new XmlBuilderException("Parent node cannot modify XML model");
    }

    @Override
    public String toString() {
        return ".." + super.toString();
    }

}
