package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Root implements StepExpr {

    @Override
    public <N extends Node> NodeView<N> resolve(ViewContext<N> context) {
        return new NodeView<N>(context.getNavigator().root());
    }

    @Override
    public boolean test(ViewContext<?> context) {
        return true;
    }

    @Override
    public <N extends Node> NodeView<N> createStepNode(ViewContext<N> context) throws XmlBuilderException {
        throw new XmlBuilderException("Root node cannot modify XML model");
    }

    @Override
    public String toString() {
        return "";
    }

}
