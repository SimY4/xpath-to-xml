package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

class Multiplication implements Operator {

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> left, View<N> right) throws XmlBuilderException {
        return new NumberView<N>(left.toNumber() * right.toNumber());
    }

    @Override
    public String toString() {
        return "*";
    }

}
