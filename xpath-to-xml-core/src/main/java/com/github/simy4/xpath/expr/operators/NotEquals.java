package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.View;

class NotEquals extends Equals {

    @Override
    public <N> boolean test(View<N> left, View<N> right) {
        return !super.test(left, right);
    }

    @Override
    public <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException {
        throw new XmlBuilderException("Can not apply a 'not equals' operator to: " + left + " and: " + right);
    }

    @Override
    public String toString() {
        return "!" + super.toString();
    }

}
