package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.ViewContext;

public class UnaryExpr extends AbstractExpr {

    private final Expr valueExpr;

    public UnaryExpr(Expr valueExpr) {
        this.valueExpr = valueExpr;
    }

    @Override
    public <N extends Node> NumberView<N> resolve(ViewContext<N> context) throws XmlBuilderException {
        return new NumberView<N>(-valueExpr.resolve(context).toNumber());
    }

    @Override
    public String toString() {
        return "-(" + valueExpr + ')';
    }

}
