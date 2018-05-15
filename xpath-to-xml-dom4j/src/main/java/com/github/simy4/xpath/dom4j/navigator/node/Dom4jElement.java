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
        return new Iterable<Dom4jNode<Element>>() {
            @Override
            public Iterator<Dom4jNode<Element>> iterator() {
                return new TransformingIterator<Element, Dom4jNode<Element>>(getNode().elementIterator(),
                        new Dom4jElementWrapper());
            }
        };
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return new Iterable<Dom4jNode<Attribute>>() {
            @Override
            public Iterator<Dom4jNode<Attribute>> iterator() {
                return new TransformingIterator<Attribute, Dom4jNode<Attribute>>(getNode().attributeIterator(),
                        new Dom4jAttributeWrapper());
            }
        };
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

    private static final class Dom4jAttributeWrapper implements Function<Attribute, Dom4jNode<Attribute>> {

        @Override
        public Dom4jNode<Attribute> apply(Attribute attribute) {
            return new Dom4jAttribute(attribute);
        }

    }

    private static final class Dom4jElementWrapper implements Function<Element, Dom4jNode<Element>> {

        @Override
        public Dom4jNode<Element> apply(Element element) {
            return new Dom4jElement(element);
        }

    }

}
