package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.QName;

/**
 * DOM4J node contract.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface Dom4jNode extends Node {

    org.dom4j.Node getNode();

    /**
     * Retrieves parent node of this node.
     *
     * @return parent node
     */
    Dom4jNode getParent();

    /**
     * Retrieves all child element nodes of this node.
     *
     * @return child element nodes
     */
    Iterable<? extends Dom4jNode> elements();

    /**
     * Retrieves all attributes of this node.
     *
     * @return attributes
     */
    Iterable<? extends Dom4jNode> attributes();

    /**
     * Creates XML attribute node and appends to ths node.
     *
     * @param attribute new XML attribute's name
     * @return new attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    Dom4jNode createAttribute(QName attribute) throws XmlBuilderException;

    /**
     * Creates XML element node and appends to ths node.
     *
     * @param element new XML element's name
     * @return new element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    Dom4jNode createElement(QName element) throws XmlBuilderException;

}
