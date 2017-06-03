package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;

import java.util.Collections;

public class PutAction implements Action {

    private final Expr expr;

    public PutAction(Expr expr) {
        this.expr = expr;
    }

    @Override
    public <T> void perform(Navigator<T> navigator) throws XmlBuilderException {
        expr.apply(navigator, navigator.xml(), true);
    }

}
