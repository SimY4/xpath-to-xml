package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;

/**
 * Comparison operation model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Op {

    <N> boolean test(Iterable<NodeView<N>> left, Iterable<NodeView<N>> right);

    <N> void apply(Navigator<N> navigator, Iterable<NodeView<N>> left, Iterable<NodeView<N>> right)
            throws XmlBuilderException;

}
