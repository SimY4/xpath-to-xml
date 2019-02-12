package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;

public class GreaterThanExpr extends AbstractOperationExpr implements Serializable {

    private static final long serialVersionUID = 1L;

    public GreaterThanExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
            throws XmlBuilderException {
        final boolean gt = 0 < Double.compare(left.toNumber(), right.toNumber());
        if (!gt && greedy) {
            throw new XmlBuilderException("Can not apply a 'greater than' operator to: " + left + " and: " + right);
        }
        return BooleanView.of(gt);
    }

    @Override
    protected String operator() {
        return ">";
    }

}
