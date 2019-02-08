package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;

/**
 * XPath step expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface StepExpr extends Expr {

    /**
     * {@inheritDoc}
     *
     * @return evaluated XML node views
     */
    @Override
    <N extends Node> IterableNodeView<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
            throws XmlBuilderException;

}
