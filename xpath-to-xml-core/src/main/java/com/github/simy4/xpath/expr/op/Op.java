package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.View;

/**
 * Comparison operation model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Op {

    <N> boolean test(View<N> left, View<N> right);

    <N> void apply(Navigator<N> navigator, View<N> left, View<N> right) throws XmlBuilderException;

}
