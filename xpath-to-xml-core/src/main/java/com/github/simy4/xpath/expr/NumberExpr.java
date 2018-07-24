package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.ViewContext;

public class NumberExpr implements Expr {

    private final NumberView<?> number;

    public NumberExpr(double number) {
        this.number = new NumberView<Node>(number);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Node> NumberView<N> resolve(ViewContext<N> context) {
        return (NumberView<N>) number;
    }

    @Override
    public String toString() {
        return number.toString();
    }

}
