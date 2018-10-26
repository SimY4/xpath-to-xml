package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Element;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class Dom4jAttribute extends AbstractDom4jNode<Attribute> {

    public Dom4jAttribute(Attribute attribute) {
        super(attribute);
    }

    @Override
    public QName getName() {
        final var namespace = getNode().getNamespace();
        return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
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
        throw new XmlBuilderException("Unable to append attribute to a non-element node " + getNode());
    }

    @Override
    public Dom4jNode<Element> createElement(org.dom4j.QName element) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append element to an attribute " + getNode());
    }

}
