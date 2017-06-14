package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;

import javax.annotation.Nullable;
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
    NodeView<N> xml();

    /**
     * Wrapped XML root.
     *
     * @return XML root
     */
    NodeView<N> root();

    /**
     * Wrapped parent of given XML node.
     *
     * @param node XML node to scan
     * @return XML node parent
     */
    @Nullable
    NodeView<N> parentOf(NodeView<N> node);

    /**
     * Child element nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child element nodes
     */
    Iterable<NodeView<N>> elementsOf(NodeView<N> parent);

    /**
     * Child attribute nodes of given XML node.
     *
     * @param parent XML node to scan
     * @return child attribute nodes
     */
    Iterable<NodeView<N>> attributesOf(NodeView<N> parent);

    /**
     * Creates detached XML attribute node.
     *
     * @param attribute new XML attribute's name
     * @return newly created attribute node
     * @throws XmlBuilderException if failure occur during XML attribute creation
     */
    NodeView<N> createAttribute(QName attribute) throws XmlBuilderException;

    /**
     * Creates detached XML element node.
     *
     * @param element new XML element's name
     * @return newly created element node
     * @throws XmlBuilderException if failure occur during XML element creation
     */
    NodeView<N> createElement(QName element) throws XmlBuilderException;

    /**
     * Clones given node.
     *
     * @param toClone XML node to clone
     * @throws XmlBuilderException if failure occur during node cloning
     */
    NodeView<N> clone(NodeView<N> toClone) throws XmlBuilderException;

    /**
     * Sets the given text content to a given node.
     *
     * @param node XML node to modify
     * @param text text content to set
     * @throws XmlBuilderException if failure occur during setting the text content
     */
    void setText(NodeView<N> node, String text) throws XmlBuilderException;

    /**
     * Appends given node to given parent.
     *
     * @param parentNode XML node to modify
     * @param child      XML node to append
     * @throws XmlBuilderException if failure occur during node appending
     */
    void append(NodeView<N> parentNode, NodeView<N> child) throws XmlBuilderException;

    /**
     * Prepends given node to given neighbor node.
     *
     * @param nextNode  XML node that should be next to prepended node
     * @param toPrepend XML node to prepend
     * @throws XmlBuilderException if failure occur during node appending
     */
    void prepend(NodeView<N> nextNode, NodeView<N> toPrepend) throws XmlBuilderException;

    /**
     * Removes/detaches given node from XML model.
     *
     * @param node node to remove
     * @throws XmlBuilderException if failure occur during node removal
     */
    void remove(NodeView<N> node) throws XmlBuilderException;

}
