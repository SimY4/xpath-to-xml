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
        String ns = XMLConstants.NULL_NS_URI;
        String localPart = wrappedNode.getNodeName();
        String prefix = XMLConstants.DEFAULT_NS_PREFIX;
        if (null == localPart) {
            ns = wrappedNode.getNamespaceURI();
            localPart = wrappedNode.getLocalName();
            prefix = wrappedNode.getPrefix();
        }
        return new QName(ns, localPart, prefix);
    }

    @Override
    public String getText() {
        return wrappedNode.getTextContent();
    }

    @Override
    public String toString() {
        return wrappedNode.toString();
    }

}
