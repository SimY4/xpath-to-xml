package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class LessThanExpr extends AbstractOperationExpr {

    public LessThanExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        final var lt = 0 > Double.compare(left.toNumber(), right.toNumber());
        if (!lt && context.isGreedy() && !context.hasNext()) {
            throw new XmlBuilderException("Can not apply a 'less than' operator to: " + left + " and: " + right);
        }
        return BooleanView.of(lt);
    }

    @Override
    String operator() {
        return "<";
    }

}
