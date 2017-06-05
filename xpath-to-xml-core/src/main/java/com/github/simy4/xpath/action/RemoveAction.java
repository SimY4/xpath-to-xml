package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Set;

public class RemoveAction implements Action {

    private final Expr expr;

    public RemoveAction(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, 1, 1);
        final Set<NodeWrapper<N>> nodes = expr.apply(context, navigator.xml(), false);
        for (NodeWrapper<N> node : nodes) {
            navigator.remove(node);
        }
    }

}
