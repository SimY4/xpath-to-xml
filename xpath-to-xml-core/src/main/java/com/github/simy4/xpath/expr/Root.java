package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

public class Root implements StepExpr {

    @Override
    public <N extends Node> NodeView<N> resolve(ExprContext<N> context) {
        return new NodeView<N>(context.getNavigator().root());
    }

    @Override
    public <N extends Node> boolean match(ExprContext<N> context) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }

}
