package com.github.simy4.xpath.utils;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.View;
import com.github.simy4.xpath.navigator.view.ViewVisitor;

import javax.xml.namespace.QName;

public final class StringNodeView implements NodeView<String> {

    public static NodeView<String> node(String value) {
        return new StringNodeView(value);
    }

    private final String value;

    private StringNodeView(String value) {
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
    public void visit(ViewVisitor<String> visitor) throws XmlBuilderException {
        visitor.visit(this);
    }

    @Override
    public int compareTo(View<String> other) {
        return -other.compareTo(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StringNodeView that = (StringNodeView) o;
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
