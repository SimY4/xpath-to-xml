package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

/**
 * XPath expression model. Every XPath expression is also a XPath predicate.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Expr {

    /**
     * Evaluate this expression using given context.
     *
     * @param navigator XML navigator
     * @param view      XML node view
     * @param greedy    whether resolution is greedy
     * @param <N>       XML model type
     * @return evaluated XML view
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> View<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException;

}
