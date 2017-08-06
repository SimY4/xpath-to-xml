package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Identity extends AbstractStepExpr {

    public Identity(Iterable<Predicate<ViewContext<?>>> predicates) {
        super(predicates);
    }

    @Override
    <N extends Node> NodeView<N> traverseStep(Navigator<N> navigator, NodeView<N> view) {
        return view;
    }

    @Override
    <N extends Node> N createStepNode(Navigator<N> navigator, NodeView<N> parentView) throws XmlBuilderException {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "." + super.toString();
    }

}
