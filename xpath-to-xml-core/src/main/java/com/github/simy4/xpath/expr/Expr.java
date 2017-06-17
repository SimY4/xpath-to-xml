package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.View;

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
     * @param <N>     XML model type
     * @return ordered set of evaluated node views
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <N> View<N> resolve(ExprContext<N> context, View<N> xml) throws XmlBuilderException;

}
