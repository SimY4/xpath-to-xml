package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;

@Immutable
public final class DomNode implements Node {

    private final org.w3c.dom.Node node;

    public DomNode(org.w3c.dom.Node node) {
        this.node = node;
    }

    org.w3c.dom.Node getNode() {
        return node;
    }

    @Override
    public QName getName() {
        final String localPart = node.getLocalName();
        if (null == localPart) {
            return new QName(node.getNodeName());
        } else {
            return new QName(node.getNamespaceURI(), localPart, node.getPrefix());
        }
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
