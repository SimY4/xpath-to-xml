package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;

/**
 * XPath expression model. Every XPath expression is also a XPath predicate.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Expr extends Predicate<ViewContext<?>> {

    /**
     * Evaluate this expression on given xml model view using given context.
     *
     * @param context XPath expression context
     * @param <N>     XML model type
     * @return evaluated XML node view
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N extends Node> View<N> resolve(ViewContext<N> context) throws XmlBuilderException;

}
