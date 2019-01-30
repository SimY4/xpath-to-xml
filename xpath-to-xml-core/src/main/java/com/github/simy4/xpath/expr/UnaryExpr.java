package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;

public class UnaryExpr implements Expr {

    private final Expr valueExpr;

    public UnaryExpr(Expr valueExpr) {
        this.valueExpr = valueExpr;
    }

    @Override
    public <N extends Node> NumberView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException {
        return new NumberView<N>(-valueExpr.resolve(navigator, view, greedy).toNumber());
    }

    @Override
    public String toString() {
        return "-(" + valueExpr + ')';
    }

}
