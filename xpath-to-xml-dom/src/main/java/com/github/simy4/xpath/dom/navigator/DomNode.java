package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;

import javax.annotation.concurrent.Immutable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

@Immutable
final class DomNode implements Node<org.w3c.dom.Node> {

    private final org.w3c.dom.Node node;

    DomNode(org.w3c.dom.Node node) {
        this.node = node;
    }

    @Override
    public org.w3c.dom.Node getWrappedNode() {
        return node;
    }

    @Override
    public QName getNodeName() {
        String ns;
        String prefix;
        String localPart = node.getLocalName();
        if (null == localPart) {
            ns = XMLConstants.NULL_NS_URI;
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            localPart = node.getNodeName();
        } else {
            ns = node.getNamespaceURI();
            prefix = node.getPrefix();
        }
        return new QName(ns, localPart, prefix);
    }

    @Override
    public String getText() {
        return node.getTextContent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DomNode that = (DomNode) o;

        return node.equals(that.node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public String toString() {
        return node.toString();
    }

}
