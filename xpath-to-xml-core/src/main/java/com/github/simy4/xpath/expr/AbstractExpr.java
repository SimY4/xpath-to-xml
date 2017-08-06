package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.ViewContext;

abstract class AbstractExpr implements Expr {

    @Override
    public final boolean test(ViewContext<?> context) throws XmlBuilderException {
        return resolve(context).toBoolean();
    }

}
