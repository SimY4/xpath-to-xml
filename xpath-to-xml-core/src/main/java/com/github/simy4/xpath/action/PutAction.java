package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;

public class PutAction implements Action {

    private final Expr expr;

    public PutAction(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <N> void perform(Navigator<N> navigator) throws XmlBuilderException {
        final ExprContext<N> context = new ExprContext<N>(navigator, 1, 1);
        expr.apply(context, navigator.xml(), true);
    }

}
