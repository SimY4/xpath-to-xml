package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

/**
 * XPath predicate model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Predicate {

    /**
     * Evaluate this predicate on given xml model view using given navigator.
     *
     * @param context XPath expression context
     * @param xml     XML model
     * @param <N>     XML model type
     * @return {@code true} if predicate resolved truly or {@code false} otherwise
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> boolean apply(ExprContext<N> context, NodeWrapper<N> xml) throws XmlBuilderException;

}
