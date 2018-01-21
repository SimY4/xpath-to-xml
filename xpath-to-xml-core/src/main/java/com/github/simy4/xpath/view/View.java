package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML elements view.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface View<N extends Node> extends Comparable<View<N>> {

    /**
     * Converts this view to a boolean value.
     *
     * @return boolean value
     */
    boolean toBoolean();

    /**
     * Converts this view to a numeric value.
     *
     * @return numeric value
     */
    double toNumber();

    /**
     * Converts this view to a string value.
     *
     * @return string value
     */
    @Override
    String toString();

    /**
     * Visits current XML element.
     *
     * @param visitor XML element visitor
     * @param <T>     type of return value
     * @return visitor result
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException;

}
