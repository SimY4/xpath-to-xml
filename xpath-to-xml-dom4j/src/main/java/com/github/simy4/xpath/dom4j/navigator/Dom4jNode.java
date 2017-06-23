package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.navigator.Node;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;

@Immutable
final class Dom4jNode implements Node<org.dom4j.Node> {

    private final org.dom4j.Node node;

    Dom4jNode(org.dom4j.Node node) {
        this.node = node;
    }

    @Override
    public org.dom4j.Node getWrappedNode() {
        return node;
    }

    @Override
    public QName getNodeName() {
        final Namespace namespace;
        switch (node.getNodeType()) {
            case org.dom4j.Node.ELEMENT_NODE:
                final Element element = (Element) node;
                namespace = element.getNamespace();
                return new QName(namespace.getURI(), element.getName(), namespace.getPrefix());
            case org.dom4j.Node.ATTRIBUTE_NODE:
                final Attribute attribute = (Attribute) node;
                namespace = attribute.getNamespace();
                return new QName(namespace.getURI(), attribute.getName(), namespace.getPrefix());
            default:
                throw new IllegalStateException("Unsupported node type");
        }
    }

    @Override
    public String getText() {
        return node.getText();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dom4jNode that = (Dom4jNode) o;

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
