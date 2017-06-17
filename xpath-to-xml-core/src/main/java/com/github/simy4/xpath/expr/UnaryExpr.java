package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class UnaryExpr implements Expr {

    private final Expr valueExpr;

    public UnaryExpr(Expr valueExpr) {
        this.valueExpr = valueExpr;
    }

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        return new NumberView<N>(-valueExpr.resolve(context, xml).toNumber());
    }

    @Override
    public <N> boolean match(ExprContext<N> context, View<N> xml) {
        return resolve(context, xml).toBoolean();
    }

    @Override
    public String toString() {
        return "-(" + valueExpr + ')';
    }

}
