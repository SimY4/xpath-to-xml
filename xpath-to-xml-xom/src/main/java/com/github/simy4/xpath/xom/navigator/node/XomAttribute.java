package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalDataException;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class XomAttribute implements XomNode<Attribute> {

    private final Attribute attribute;

    public XomAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public Attribute getNode() {
        return attribute;
    }

    @Override
    public QName getName() {
        return new QName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getNamespacePrefix());
    }

    @Override
    public String getText() {
        return attribute.getValue();
    }

    @Override
    public Iterable<XomNode<Element>> elements() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<XomNode<Attribute>> attributes() {
        return Collections.emptyList();
    }

    @Override
    public XomNode<Attribute> appendAttribute(Attribute attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a non-element node " + this.attribute);
    }

    @Override
    public XomNode<Element> appendElement(Element element) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append element to an attribute " + attribute);
    }

    @Override
    public void setText(String text) throws XmlBuilderException {
        try {
            attribute.setValue(text);
        } catch (IllegalDataException ide) {
            throw new XmlBuilderException("Unable to set value to " + attribute, ide);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        var that = (XomAttribute) o;

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
