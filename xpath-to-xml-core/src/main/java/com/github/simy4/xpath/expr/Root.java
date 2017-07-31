package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.View;

public class Root implements StepExpr {

    @Override
    public <N extends Node> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) {
        context.advance();
        return NodeSetView.singleton(context.getNavigator().root());
    }

    @Override
    public <N extends Node> boolean match(ExprContext<N> context, View<N> xml) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }

}
