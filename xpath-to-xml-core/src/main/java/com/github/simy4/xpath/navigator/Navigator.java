package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;

import javax.xml.namespace.QName;

public interface Navigator<N> {

    NodeWrapper<N> xml();

    NodeWrapper<N> root();

    NodeWrapper<N> parentOf(NodeWrapper<N> node);

    Iterable<NodeWrapper<N>> elementsOf(NodeWrapper<N> parent);

    Iterable<NodeWrapper<N>> attributesOf(NodeWrapper<N> parent);

    NodeWrapper<N> createAttribute(QName attribute) throws XmlBuilderException;

    NodeWrapper<N> createElement(QName element) throws XmlBuilderException;

    void setText(NodeWrapper<N> node, String text) throws XmlBuilderException;

    void append(NodeWrapper<N> parentNode, NodeWrapper<N> child) throws XmlBuilderException;

    void remove(NodeWrapper<N> node) throws XmlBuilderException;

}
