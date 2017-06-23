package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

public class Root implements StepExpr {

    @Override
    public <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) {
        context.advance();
        return NodeSetView.singleton(new NodeView<>(context.getNavigator().root()));
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }

}
