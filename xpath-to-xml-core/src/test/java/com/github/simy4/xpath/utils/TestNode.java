package com.github.simy4.xpath.utils;

import com.github.simy4.xpath.navigator.Node;

import javax.xml.namespace.QName;

public final class TestNode implements Node {

    public static TestNode node(String value) {
        return new TestNode(value);
    }

    private final String value;

    private TestNode(String value) {
        this.value = value;
    }

    @Override
    public QName getName() {
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

        TestNode that = (TestNode) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return getName().toString();
    }

}
