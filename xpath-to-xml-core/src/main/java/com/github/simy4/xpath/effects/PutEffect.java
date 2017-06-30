package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

public class PutEffect implements Effect {

    private final Expr expr;

    public PutEffect(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N extends Node> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<>(navigator, true, 1);
        expr.resolve(context, new NodeView<>(navigator.xml()));
    }

}
