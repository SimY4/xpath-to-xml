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
public interface Navigator<N extends Node> {

    /**
     * Wrapped XML root.
     *
     * @return XML root
     */
    N root();

    /**
     * Wrapped parent of given XML node.
     *
     * @param node XML node to scan
     * @return XML node parent
     */
    N parentOf(N node);

    /**
     * Child element nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child element nodes
     */
    Iterable<? extends N> elementsOf(N parent);

    /**
     * Child attribute nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child attribute nodes
     */
    Iterable<? extends N> attributesOf(N parent);

    /**
     * Creates XML attribute node and appends to given parent.
     *
     * @param parent    parent XML node to modify
     * @param attribute new XML attribute's name
     * @return newly created attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    N createAttribute(N parent, QName attribute) throws XmlBuilderException;

    /**
     * Creates XML element node and appends to given parent.
     *
     * @param parent  parent XML node to modify
     * @param element new XML element's name
     * @return newly created element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    N createElement(N parent, QName element) throws XmlBuilderException;

    /**
     * Sets the given text content to a given node.
     *
     * @param node XML node to modify
     * @param text text content to set
     * @throws XmlBuilderException if failure occur during setting the text content
     */
    void setText(N node, String text) throws XmlBuilderException;

    /**
     * Prepends a copy of given node to this node.
     *
     * @param node XML node to copy and prepend
     * @throws XmlBuilderException if failure occur during node appending
     */
    void prependCopy(N node) throws XmlBuilderException;

    /**
     * Removes/detaches given node from XML model.
     *
     * @param node node to remove
     * @throws XmlBuilderException if failure occur during node removal
     */
    void remove(N node) throws XmlBuilderException;

}
