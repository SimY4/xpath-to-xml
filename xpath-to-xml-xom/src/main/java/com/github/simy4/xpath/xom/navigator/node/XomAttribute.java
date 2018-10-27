package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalDataException;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class XomAttribute extends AbstractXomNode<Attribute> {

    public XomAttribute(Attribute attribute) {
        super(attribute);
    }

    @Override
    public QName getName() {
        return new QName(getNode().getNamespaceURI(), getNode().getLocalName(), getNode().getNamespacePrefix());
    }

    @Override
    public String getText() {
        return getNode().getValue();
    }

    @Override
    public Iterable<? extends XomNode> elements() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<? extends XomNode> attributes() {
        return Collections.emptyList();
    }

    @Override
    public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a non-element node " + getNode());
    }

    @Override
    public XomNode appendElement(Element element) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append element to an attribute " + getNode());
    }

    @Override
    public void setText(String text) throws XmlBuilderException {
        try {
            getNode().setValue(text);
        } catch (IllegalDataException ide) {
            throw new XmlBuilderException("Unable to set value to " + getNode(), ide);
        }
    }

}
