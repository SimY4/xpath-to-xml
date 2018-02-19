package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.util.TransformingIterator;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;

public final class Dom4jElement implements Dom4jNode<Element> {

    private final Element element;

    public Dom4jElement(Element element) {
        this.element = element;
    }

    @Override
    public Element getNode() {
        return element;
    }

    @Override
    public QName getName() {
        final Namespace namespace = element.getNamespace();
        return new QName(namespace.getURI(), element.getName(), namespace.getPrefix());
    }

    @Override
    public String getText() {
        return element.getText();
    }

    @Override
    public Iterable<Dom4jNode<Element>> elements() {
        return () -> new TransformingIterator<>(element.elementIterator(), Dom4jElement::new);
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return () -> new TransformingIterator<>(element.attributeIterator(), Dom4jAttribute::new);
    }

    @Override
    public Dom4jNode<Attribute> createAttribute(org.dom4j.QName attribute) {
        final Attribute attr = DocumentHelper.createAttribute(element, attribute, "");
        element.attributes().add(attr);
        return new Dom4jAttribute(attr);
    }

    @Override
    public Dom4jNode<Element> createElement(org.dom4j.QName element) {
        return new Dom4jElement(this.element.addElement(element));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dom4jElement that = (Dom4jElement) o;

        return element.equals(that.element);
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public String toString() {
        return element.toString();
    }

}
