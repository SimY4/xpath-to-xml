package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
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

    <N> View<N> resolve(View<N> left, View<N> right);

    <N> View<N> apply(ExprContext<N> context, View<N> left, View<N> right) throws XmlBuilderException;

}
