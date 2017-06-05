package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.Set;

/**
 * XPath expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Expr {

    /**
     * Evaluate this expression on given xml model view using given navigator.
     *
     * @param context XPath expression context
     * @param xml     XML model
     * @param greedy  {@code true} if you want to evaluate expression greedily and {@code false} otherwise
     * @param <N>     XML model type
     * @return list of evaluated node views
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> Set<NodeWrapper<N>> apply(ExprContext<N> context, NodeWrapper<N> xml, boolean greedy)
            throws XmlBuilderException;

}
