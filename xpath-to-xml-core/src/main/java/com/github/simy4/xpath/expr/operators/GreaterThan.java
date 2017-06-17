package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.View;

class GreaterThan implements Operator {

    @Override
    public <N> boolean test(View<N> left, View<N> right) {
        return 0 < left.compareTo(right);
    }

    @Override
    public <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException {
        throw new XmlBuilderException("Can not apply a 'greater than' operator to: " + left + " and: " + right);
    }

    @Override
    public String toString() {
        return ">";
    }

}
