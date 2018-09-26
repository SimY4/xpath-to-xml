package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import nu.xom.Attribute;
import nu.xom.Element;

/**
 * XOM node contract.
 *
 * @param <N> XOM node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface XomNode<N extends nu.xom.Node> extends Node {

    N getNode();

    /**
     * Retrieves all child element nodes of this node.
     *
     * @return child element nodes
     */
    Iterable<XomNode<Element>> elements();

    /**
     * Retrieves all attributes of this node.
     *
     * @return attributes
     */
    Iterable<XomNode<Attribute>> attributes();

    /**
     * Creates XML attribute node and appends to ths node.
     *
     * @param attribute new XML attribute's name
     * @return new attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    XomNode<Attribute> appendAttribute(Attribute attribute) throws XmlBuilderException;

    /**
     * Creates XML element node and appends to ths node.
     *
     * @param element new XML element's name
     * @return new element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    XomNode<Element> appendElement(Element element) throws XmlBuilderException;

    /**
     * Sets the given text content to this node.
     *
     * @param text text content to set
     * @throws XmlBuilderException if failure occur during setting the text content
     */
    void setText(String text) throws XmlBuilderException;

}
