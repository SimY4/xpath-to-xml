package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;

public class NumberExpr implements Expr {

    private final NumberView number;

    public NumberExpr(Number number) {
        this.number = new NumberView(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N> NumberView<N> resolve(ExprContext<N> context, View<N> xml) {
        return (NumberView<N>) number;
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
