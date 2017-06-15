package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.View;

public class Root implements StepExpr {

    @Override
    public <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) {
        context.advance();
        return NodeSetView.singleton(context.getNavigator().root());
    }

    @Override
    public String toString() {
        return "";
    }

}
