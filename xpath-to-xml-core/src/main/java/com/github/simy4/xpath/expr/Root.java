package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.View;

public class Root implements StepExpr, Predicate {

    @Override
    public <N> NodeSetView<N> resolve(ExprContext<N> context, View<N> xml) {
        context.advance();
        return NodeSetView.singleton(new NodeView<N>(context.getNavigator().root()));
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return !resolve(context, xml).isEmpty();
    }

    @Override
    public String toString() {
        return "";
    }

}
