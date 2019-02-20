package com.github.simy4.xpath.util;

import com.github.simy4.xpath.navigator.Node;

import javax.xml.namespace.QName;
import java.io.Serializable;

public final class TestNode implements Node, Serializable {

    private static final long serialVersionUID = 1L;

    private final QName value;

    public static TestNode node(String value) {
        return node(new QName(value));
    }

    public static TestNode node(QName value) {
        return new TestNode(value);
    }

    private TestNode(QName value) {
        this.value = value;
    }

    @Override
    public QName getName() {
        return value;
    }

    @Override
    public String getText() {
        return value.getLocalPart();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        var that = (TestNode) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return getName().toString() + ':' + getText();
    }

}
