package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

public class Identity extends AbstractStepExpr {

    public Identity(Iterable<? extends Predicate> predicates) {
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
