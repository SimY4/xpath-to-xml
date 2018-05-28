package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Identity extends AbstractStepExpr {

    public Identity(Iterable<Expr> predicates) {
        super(predicates);
    }

    @Override
    <N extends Node> NodeView<N> resolveStep(ViewContext<N> context) throws XmlBuilderException {
        return context.getCurrent();
    }

    @Override
    <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Identity node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "." + super.toString();
    }

}
