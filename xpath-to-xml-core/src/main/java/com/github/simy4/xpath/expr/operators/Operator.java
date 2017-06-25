package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Node;
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

    Operator addition = new Addition();

    Operator subtraction = new Subtraction();

    Operator multiplication = new Multiplication();

    /**
     * Evaluate this operator on two given xml model views using given context.
     *
     * @param context XPath expression context
     * @param left    left XML model
     * @param right   right XML model
     * @param <N>     XML model type
     * @return evaluated XML node view
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> View<N> resolve(ExprContext<N> context, View<N> left, View<N> right) throws XmlBuilderException;

}
