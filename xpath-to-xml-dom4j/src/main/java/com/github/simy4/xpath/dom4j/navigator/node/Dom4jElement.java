package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.util.TransformingIterator;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;

public final class Dom4jElement extends AbstractDom4jNode<Element> {

    public Dom4jElement(Element element) {
        super(element);
    }

    @Override
    public QName getName() {
        final Namespace namespace = getNode().getNamespace();
        return new QName(namespace.getURI(), getNode().getName(), namespace.getPrefix());
    }

    @Override
    public Iterable<Dom4jNode<Element>> elements() {
        return () -> new TransformingIterator<>(getNode().elementIterator(), Dom4jElement::new);
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return () -> new TransformingIterator<>(getNode().attributeIterator(), Dom4jAttribute::new);
    }

    @Override
    public Dom4jNode<Attribute> createAttribute(org.dom4j.QName attribute) {
        final Attribute attr = DocumentHelper.createAttribute(getNode(), attribute, "");
        getNode().attributes().add(attr);
        return new Dom4jAttribute(attr);
    }

    @Override
    public Dom4jNode<Element> createElement(org.dom4j.QName element) {
        return new Dom4jElement(this.getNode().addElement(element));
    }

}
