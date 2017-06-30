package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.View;

abstract class AbstractExpr implements Expr {

    @Override
    public final <N extends Node> boolean match(ExprContext<N> context, View<N> xml) throws XmlBuilderException {
        return resolve(context, xml).toBoolean();
    }

}
