package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.View;

/**
 * XPath expression model.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface Expr {

    /**
     * Evaluate this expression on given xml model view using given context.
     *
     * @param context XPath expression context
     * @param xml     XML model
     * @param <N>     XML model type
     * @return evaluated XML node view
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> View<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException;

}
