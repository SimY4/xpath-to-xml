package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;

public class LessThanOrEqualsExpr extends AbstractOperationExpr {

    private static final long serialVersionUID = 1L;

    public LessThanOrEqualsExpr(Expr leftExpr, Expr rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public <N extends Node> View<N> resolve(Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
            throws XmlBuilderException {
        boolean le = 0 >= Double.compare(left.toNumber(), right.toNumber());
        if (!le && greedy) {
            le = left.visit(new EqualsExpr.EqualsVisitor<N>(navigator, right));
        }
        return BooleanView.of(le);
    }

    @Override
    protected String operator() {
        return "<=";
    }

}
