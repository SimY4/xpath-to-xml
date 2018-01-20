package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

public class GreaterThanOrEqualsExpr extends AbstractOperationExpr {

    public GreaterThanOrEqualsExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        boolean ge = 0 <= Double.compare(left.toNumber(), right.toNumber());
        if (!ge && context.isGreedy() && !context.hasNext()) {
            ge = left.visit(new EqualsExpr.EqualsVisitor<N>(context.getNavigator(), right));
        }
        return BooleanView.of(ge);
    }

    @Override
    String operator() {
        return ">=";
    }

}
