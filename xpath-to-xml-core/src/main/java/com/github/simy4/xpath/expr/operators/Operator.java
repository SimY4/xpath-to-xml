package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.View;

/**
 * Operator model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Operator {

    Operator equals = new Equals();

    Operator notEquals = new NotEquals();

    Operator lessThan = new LessThan();

    Operator lessThanOrEquals = new LessThanOrEquals();

    Operator greaterThan = new GreaterThan();

    Operator greaterThanOrEquals = new GreaterThanOrEquals();

    <N> boolean test(View<N> left, View<N> right);

    <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException;

}
