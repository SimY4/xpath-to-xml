package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Collections;
import java.util.Set;

public class Root extends AbstractExpr implements StepExpr {

    @Override
    public <N> Set<NodeWrapper<N>> resolve(ExprContext<N> context, NodeWrapper<N> xml) {
        context.advance();
        return Collections.singleton(context.getNavigator().root());
    }

    @Override
    public String toString() {
        return "";
    }

}
