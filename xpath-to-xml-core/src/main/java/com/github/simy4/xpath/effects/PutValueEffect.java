package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Set;

public class PutValueEffect implements Effect {

    private final Expr expr;
    private final String value;

    public PutValueEffect(Expr expr, Object value) {
        this.expr = expr;
        this.value = String.valueOf(value);
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, true, 1);
        final Set<NodeView<N>> nodes = expr.resolve(context, navigator.xml());
        for (NodeView<N> node : nodes) {
            navigator.setText(node, value);
        }
    }

}
