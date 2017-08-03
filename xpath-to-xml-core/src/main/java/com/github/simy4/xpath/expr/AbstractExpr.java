package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.ViewContext;

abstract class AbstractExpr implements Expr {

    @Override
    public final <N extends Node> boolean match(ViewContext<N> context) throws XmlBuilderException {
        return resolve(context).toBoolean();
    }

}
