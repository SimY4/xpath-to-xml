package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import java.util.Iterator;

@Immutable
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
        return new Iterable<Dom4jNode<Element>>() {
            @Override
            @Nonnull
            public Iterator<Dom4jNode<Element>> iterator() {
                return new Dom4jElementsIterator(element.elementIterator());
            }
        };
    }

    @Override
    public Iterable<Dom4jNode<Attribute>> attributes() {
        return new Iterable<Dom4jNode<Attribute>>() {
            @Override
            @Nonnull
            public Iterator<Dom4jNode<Attribute>> iterator() {
                return new Dom4jAttributesIterator(element.attributeIterator());
            }
        };
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

    private static final class Dom4jAttributesIterator implements Iterator<Dom4jNode<Attribute>> {

        private final Iterator<Attribute> elementIterator;

        Dom4jAttributesIterator(Iterator<Attribute> elementIterator) {
            this.elementIterator = elementIterator;
        }

        @Override
        public boolean hasNext() {
            return elementIterator.hasNext();
        }

        @Override
        public Dom4jNode<Attribute> next() {
            return new Dom4jAttribute(elementIterator.next());
        }

        @Override
        public void remove() {
            elementIterator.remove();
        }

    }

    private static final class Dom4jElementsIterator implements Iterator<Dom4jNode<Element>> {

        private final Iterator<Element> elementIterator;

        Dom4jElementsIterator(Iterator<Element> elementIterator) {
            this.elementIterator = elementIterator;
        }

        @Override
        public boolean hasNext() {
            return elementIterator.hasNext();
        }

        @Override
        public Dom4jNode<Element> next() {
            return new Dom4jElement(elementIterator.next());
        }

        @Override
        public void remove() {
            elementIterator.remove();
        }

    }

}
