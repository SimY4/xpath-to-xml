package com.github.simy4.xpath.utils;

import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

public final class StringNode implements Node<String> {

    public static Node<String> node(String value) {
        return new StringNode(value);
    }

    private final String value;

    private StringNode(String value) {
        this.value = value;
    }

    @Override
    public String getWrappedNode() {
        return value;
    }

    @Override
    public QName getNodeName() {
        return new QName(value);
    }

    @Override
    public String getText() {
        return value;
    }

    public NodeView<String> view() {
        return new NodeView<String>(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringNode that = (StringNode) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return getNodeName().toString();
    }

}
