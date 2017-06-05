package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;

import java.util.List;

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
     * @param navigator XML model navigator
     * @param xml       XML model
     * @param greedy    {@code true} if you want to evaluate expression greedily and {@code false} otherwise
     * @param <N>       XML model type
     * @return list of evaluated node views
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> List<NodeWrapper<N>> apply(Navigator<N> navigator, NodeWrapper<N> xml, boolean greedy)
            throws XmlBuilderException;

}
