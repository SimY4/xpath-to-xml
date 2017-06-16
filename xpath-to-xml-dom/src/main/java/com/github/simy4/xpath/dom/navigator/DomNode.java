package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

final class DomNode implements Node<org.w3c.dom.Node> {

    private final org.w3c.dom.Node wrappedNode;

    DomNode(org.w3c.dom.Node wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    @Override
    public org.w3c.dom.Node getWrappedNode() {
        return wrappedNode;
    }

    @Override
    public QName getNodeName() {
        String ns;
        String prefix;
        String localPart = wrappedNode.getLocalName();
        if (null == localPart) {
            ns = XMLConstants.NULL_NS_URI;
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            localPart = wrappedNode.getNodeName();
        } else {
            ns = wrappedNode.getNamespaceURI();
            prefix = wrappedNode.getPrefix();
        }
        return new QName(ns, localPart, prefix);
    }

    @Override
    public String getText() {
        return wrappedNode.getTextContent();
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

        return wrappedNode.equals(that.wrappedNode);
    }

    @Override
    public int hashCode() {
        return wrappedNode.hashCode();
    }

    @Override
    public String toString() {
        return wrappedNode.toString();
    }

}
