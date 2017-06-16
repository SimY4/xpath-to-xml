package com.github.simy4.xpath.navigator;

import javax.xml.namespace.QName;

/**
 * XML node contract.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface Node<N> {

    /**
     * Original node.
     *
     * @return node.
     */
    N getWrappedNode();

    /**
     * XML node name.
     *
     * @return node name.
     */
    QName getNodeName();

    /**
     * XML node text content.
     *
     * @return text content.
     */
    String getText();

}
