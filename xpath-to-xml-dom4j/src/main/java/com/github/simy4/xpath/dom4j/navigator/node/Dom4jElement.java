package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.TransformingIterator;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.xml.namespace.QName;
import java.util.Iterator;

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
        return new Iterable<Dom4jElement>() {
            @Override
            public Iterator<Dom4jElement> iterator() {
                return new TransformingIterator<Element, Dom4jElement>(getNode().elementIterator(),
                        new Dom4jElementWrapper());
            }
        };
    }

    @Override
    public Iterable<? extends Dom4jNode> attributes() {
        return new Iterable<Dom4jAttribute>() {
            @Override
            public Iterator<Dom4jAttribute> iterator() {
                return new TransformingIterator<Attribute, Dom4jAttribute>(getNode().attributeIterator(),
                        new Dom4jAttributeWrapper());
            }
        };
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

    private static final class Dom4jAttributeWrapper implements Function<Attribute, Dom4jAttribute> {

        @Override
        public Dom4jAttribute apply(Attribute attribute) {
            return new Dom4jAttribute(attribute);
        }

    }

    private static final class Dom4jElementWrapper implements Function<Element, Dom4jElement> {

        @Override
        public Dom4jElement apply(Element element) {
            return new Dom4jElement(element);
        }

    }

}
