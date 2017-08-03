package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

abstract class AbstractExpr implements Expr {

    @Override
    public final <N extends Node> boolean match(ExprContext<N> context) throws XmlBuilderException {
        return resolve(context).toBoolean();
    }

}
