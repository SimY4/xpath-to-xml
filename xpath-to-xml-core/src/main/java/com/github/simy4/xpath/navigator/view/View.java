package com.github.simy4.xpath.navigator.view;

import com.github.simy4.xpath.XmlBuilderException;

/**
 * XML element contract.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface View<N> {

    void visit(ViewVisitor<N> visitor) throws XmlBuilderException;

}
