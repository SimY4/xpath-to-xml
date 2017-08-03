package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

class LessThanOrEquals implements Operator {

    @Override
    public <N extends Node> View<N> resolve(ViewContext<N> context, View<N> left, View<N> right)
            throws XmlBuilderException {
        boolean le = 0 >= Double.compare(left.toNumber(), right.toNumber());
        if (!le && context.isGreedy() && !context.hasNext()) {
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
