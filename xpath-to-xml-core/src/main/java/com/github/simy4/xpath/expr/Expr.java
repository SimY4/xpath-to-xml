package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import java.util.Set;

/**
 * XPath expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Expr {

    /**
     * Transforms this expression into XPath predicate.
     *
     * @return XPath predicate
     */
    Predicate asPredicate();

    /**
     * Evaluate this expression on given xml model view using given navigator.
     *
     * @param context XPath expression context
     * @param xml     XML model
     * @param <N>     XML model type
     * @return ordered set of evaluated node views
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> Set<NodeView<N>> resolve(ExprContext<N> context, NodeView<N> xml) throws XmlBuilderException;

}
