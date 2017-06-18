package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;

class LessThanOrEquals implements Operator {

    @Override
    public <N> View<N> resolve(ExprContext<N> context, View<N> left, View<N> right) throws XmlBuilderException {
        boolean le = 0 >= Double.compare(left.toNumber(), right.toNumber());
        if (!le && context.shouldCreate()) {
            throw new XmlBuilderException("Can not apply a 'less than or equals' operator "
                    + "to: " + left + " and: " + right);
        }
        return BooleanView.of(le);
    }

    @Override
    public String toString() {
        return "<=";
    }

}
