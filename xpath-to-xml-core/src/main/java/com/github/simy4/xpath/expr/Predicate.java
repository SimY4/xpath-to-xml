package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.ViewContext;

/**
 * XPath predicate model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Predicate {

    /**
     * Evaluate this predicate on given xml model view using given context.
     *
     * @param context XPath expression context
     * @param <N>     XML model type
     * @return {@code true} if predicate matches existing model or {@code false} otherwise
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> boolean match(ViewContext<N> context) throws XmlBuilderException;

}
