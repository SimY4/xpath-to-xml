package com.github.simy4.xpath.navigator;

import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

final class DomNodeWrapper implements NodeWrapper<Node> {

    private final Node wrappedNode;

    DomNodeWrapper(Node wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    @Override
    public Node getWrappedNode() {
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

        DomNodeWrapper that = (DomNodeWrapper) o;

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
