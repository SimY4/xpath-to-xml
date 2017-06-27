package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;
import java.util.Iterator;

@Immutable
public final class XomElement implements XomNode<Element> {

    private final Element element;

    public XomElement(Element element) {
        this.element = element;
    }

    @Override
    public Element getNode() {
        return element;
    }

    @Override
    public QName getName() {
        return new QName(element.getNamespaceURI(), element.getLocalName(), element.getNamespacePrefix());
    }

    @Override
    public String getText() {
        return element.getValue();
    }

    @Override
    public Iterable<XomNode<Element>> elements() {
        return new Iterable<XomNode<Element>>() {
            @Override
            @Nonnull
            public Iterator<XomNode<Element>> iterator() {
                return new XomElementsIterator(element.getChildElements());
            }
        };
    }

    @Override
    public Iterable<XomNode<Attribute>> attributes() {
        return new Iterable<XomNode<Attribute>>() {
            @Override
            @Nonnull
            public Iterator<XomNode<Attribute>> iterator() {
                return new XomAttributesIterator(element);
            }
        };
    }

    @Override
    public XomNode<Attribute> appendAttribute(Attribute attribute) throws XmlBuilderException {
        try {
            element.appendChild(attribute);
            return new XomAttribute(attribute);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an attribute to " + element, iae);
        }
    }

    @Override
    public XomNode<Element> appendElement(Element element) throws XmlBuilderException {
        try {
            element.appendChild(element);
            return new XomElement(element);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an element to " + element, iae);
        }
    }

    @Override
    public void setValue(String value) throws XmlBuilderException {
        try {
            element.appendChild(value);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to set value to " + element, iae);
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

        XomElement that = (XomElement) o;

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

    private static final class XomAttributesIterator implements Iterator<XomNode<Attribute>> {

        private final Element element;
        private int cursor;

        XomAttributesIterator(Element element) {
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return cursor < element.getAttributeCount();
        }

        @Override
        public XomNode<Attribute> next() {
            return new XomAttribute(element.getAttribute(cursor++));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

    }

    private static final class XomElementsIterator implements Iterator<XomNode<Element>> {

        private final Elements elements;
        private int cursor;

        XomElementsIterator(Elements elements) {
            this.elements = elements;
        }

        @Override
        public boolean hasNext() {
            return cursor < elements.size();
        }

        @Override
        public XomNode<Element> next() {
            return new XomElement(elements.get(cursor++));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

    }

}
