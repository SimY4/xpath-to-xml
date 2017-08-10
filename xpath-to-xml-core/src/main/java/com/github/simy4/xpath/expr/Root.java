package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;

public class Root implements StepExpr {

    @Override
    public <N extends Node> NodeView<N> resolve(ViewContext<N> context) {
        return new NodeView<>(context.getNavigator().root());
    }

    @Override
    public boolean test(ViewContext<?> context) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }

}
