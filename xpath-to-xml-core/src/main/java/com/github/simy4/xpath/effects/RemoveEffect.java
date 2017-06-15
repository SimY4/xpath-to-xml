package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Set;

public class RemoveEffect implements Effect {

    private final Expr expr;

    public RemoveEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, false, 1);
        final Set<NodeWrapper<N>> nodes = expr.resolve(context, navigator.xml());
        for (NodeWrapper<N> node : nodes) {
            navigator.remove(node);
        }
    }

}
