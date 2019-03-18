package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;

public final class Dom4jElement extends AbstractDom4jNode<Element> {

    private static final long serialVersionUID = 1L;

    public Dom4jElement(Element element) {
        super(element);
    }

    @Override
    public QName getName() {
        final Namespace namespace = getNode().getNamespace();
        return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
    }

    @Override
    public Dom4jNode getParent() {
        final Element node = getNode();
        final Element parent = node.getParent();
        return null == parent ? node.getDocument().getRootElement() == node ? new Dom4jDocument(node.getDocument())
                : null : new Dom4jElement(parent);
    }

    @Override
    public Iterable<? extends Dom4jNode> elements() {
        return (Iterable<Dom4jElement>) () -> getNode().elements().stream()
                .map(Dom4jElement::new)
                .iterator();
    }

    @Override
    public Iterable<? extends Dom4jNode> attributes() {
        return (Iterable<Dom4jAttribute>) () -> getNode().attributes().stream()
                .map(Dom4jAttribute::new)
                .iterator();
    }

    @Override
    public Dom4jNode createAttribute(org.dom4j.QName attribute) {
        final Attribute attr = DocumentHelper.createAttribute(getNode(), attribute, "");
        getNode().attributes().add(attr);
        return new Dom4jAttribute(attr);
    }

    @Override
    public Dom4jNode createElement(org.dom4j.QName element) {
        return new Dom4jElement(getNode().addElement(element));
    }

}
