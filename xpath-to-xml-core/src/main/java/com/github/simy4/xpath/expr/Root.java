package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Collections;
import java.util.Set;

public class Root extends AbstractExpr implements StepExpr {

    @Override
    public <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) {
        context.advance();
        return Collections.singleton(context.getNavigator().root());
    }

    @Override
    public String toString() {
        return "";
    }

}
