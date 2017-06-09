package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;

import javax.xml.namespace.QName;

/**
 * XML model navigator contract.
 *
 * @param <N> XML model nodes type
 * @author Alex Simkin
 * @since 1.0
 */
public interface Navigator<N> {

    /**
     * Wrapped initial XML node.
     *
     * @return initial XML node
     */
    NodeWrapper<N> xml();

    /**
     * Wrapped XML root.
     *
     * @return XML root
     */
    NodeWrapper<N> root();

    /**
     * Wrapped parent of given XML node.
     *
     * @param node XML node to scan
     * @return XML node parent
     */
    NodeWrapper<N> parentOf(NodeWrapper<N> node);

    /**
     * Child element nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child element nodes
     */
    Iterable<NodeWrapper<N>> elementsOf(NodeWrapper<N> parent);

    /**
     * Child attribute nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child attribute nodes
     */
    Iterable<NodeWrapper<N>> attributesOf(NodeWrapper<N> parent);

    /**
     * Creates detached XML attribute node.
     *
     * @param attribute new XML attribute's name
     * @return newly created attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    NodeWrapper<N> createAttribute(QName attribute) throws XmlBuilderException;

    /**
     * Creates detached XML element node.
     *
     * @param element new XML element's name
     * @return newly created element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    NodeWrapper<N> createElement(QName element) throws XmlBuilderException;

    /**
     * Clones given node.
     *
     * @param toClone XML node to clone
     * @throws XmlBuilderException if failure occur during node cloning
     */
    NodeWrapper<N> clone(NodeWrapper<N> toClone) throws XmlBuilderException;

    /**
     * Sets the given text content to a given node.
     *
     * @param node XML node to modify
     * @param text text content to set
     * @throws XmlBuilderException if failure occur during setting the text content
     */
    void setText(NodeWrapper<N> node, String text) throws XmlBuilderException;

    /**
     * Appends given node to given parent.
     *
     * @param parentNode XML node to modify
     * @param child      XML node to append
     * @throws XmlBuilderException if failure occur during node appending
     */
    void append(NodeWrapper<N> parentNode, NodeWrapper<N> child) throws XmlBuilderException;

    /**
     * Prepends given node to given neighbor node.
     *
     * @param nextNode  XML node that should be next to prepended node
     * @param toPrepend XML node to prepend
     * @throws XmlBuilderException if failure occur during node appending
     */
    void prepend(NodeWrapper<N> nextNode, NodeWrapper<N> toPrepend) throws XmlBuilderException;

    /**
     * Removes/detaches given node from XML model.
     *
     * @param node node to remove
     * @throws XmlBuilderException if failure occur during node removal
     */
    void remove(NodeWrapper<N> node) throws XmlBuilderException;

}
