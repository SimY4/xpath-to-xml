package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

/**
 * XML elements view.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface View<N> extends Comparable<View<N>> {

    /**
     * Visits current XML element.
     *
     * @param visitor XML element visitor
     * @param <T> visitor return type
     * @return resolved return value
     * @throws XmlBuilderException if error occur during XML model modification
     */
    <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException;

}
