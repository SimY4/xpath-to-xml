package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import java.util.Collections;

@Immutable
public final class Dom4jAttribute implements Dom4jNode<Attribute> {

    private final Attribute attribute;

    public Dom4jAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public Attribute getNode() {
        return attribute;
    }

    @Override
    public QName getName() {
        final Namespace namespace = attribute.getNamespace();
        return new QName(namespace.getURI(), attribute.getName(), namespace.getPrefix());
    }

    @Override
    public String getText() {
        return attribute.getText();
    }

    @Override
    public Iterable<Dom4jNode<Element>> elements() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return Collections.emptyList();
    }

    @Override
    public Dom4jNode<Attribute> createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a non-element node " + this.attribute);
    }

    @Override
    public Dom4jNode<Element> createElement(org.dom4j.QName element) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append element to an attribute " + attribute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dom4jAttribute that = (Dom4jAttribute) o;

        return attribute.equals(that.attribute);
    }

    @Override
    public int hashCode() {
        return attribute.hashCode();
    }

    @Override
    public String toString() {
        return attribute.toString();
    }

}
