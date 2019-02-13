package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;
import java.util.Collections;

public final class Dom4jAttribute extends AbstractDom4jNode<Attribute> {

    private static final long serialVersionUID = 1L;

    public Dom4jAttribute(Attribute attribute) {
        super(attribute);
    }

    @Override
    public QName getName() {
        final Namespace namespace = getNode().getNamespace();
        return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
    }

    @Override
    public Dom4jNode getParent() {
        final Element parent = getNode().getParent();
        return null == parent ? null : new Dom4jElement(parent);
    }

    @Override
    public Iterable<? extends Dom4jNode> elements() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<? extends Dom4jNode> attributes() {
        return Collections.emptyList();
    }

    @Override
    public Dom4jNode createAttribute(org.dom4j.QName attribute) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append attribute to a non-element node " + getNode());
    }

    @Override
    public Dom4jNode createElement(org.dom4j.QName element) throws XmlBuilderException {
        throw new XmlBuilderException("Unable to append element to an attribute " + getNode());
    }

}
