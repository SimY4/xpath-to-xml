package com.github.simy4.xpath.utils;

import com.github.simy4.xpath.navigator.Node;

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
