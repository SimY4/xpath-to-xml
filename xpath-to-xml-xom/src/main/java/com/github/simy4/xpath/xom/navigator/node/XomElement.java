package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.ReadOnlyIterator;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.Text;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class XomElement implements XomNode {

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
    public Iterable<? extends XomNode> elements() {
        return new Iterable<XomElement>() {
            @Override
            public Iterator<XomElement> iterator() {
                return new XomElementsIterator(element.getChildElements());
            }
        };
    }

    @Override
    public Iterable<? extends XomNode> attributes() {
        return new Iterable<XomAttribute>() {
            @Override
            public Iterator<XomAttribute> iterator() {
                return new XomAttributesIterator(element);
            }
        };
    }

    @Override
    public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
        try {
            element.addAttribute(attribute);
            return new XomAttribute(attribute);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an attribute to " + element, iae);
        }
    }

    @Override
    public XomNode appendElement(Element element) throws XmlBuilderException {
        try {
            this.element.appendChild(element);
            return new XomElement(element);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an element to " + element, iae);
        }
    }

    @Override
    public void setText(String text) throws XmlBuilderException {
        try {
            for (int i = 0; i < element.getChildCount(); i++) {
                final Node child = element.getChild(i);
                if (child instanceof Text) {
                    ((Text) child).setValue(text);
                    return;
                }
            }
            element.appendChild(text);
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

    private static final class XomAttributesIterator extends ReadOnlyIterator<XomAttribute> {

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
        public XomAttribute next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return new XomAttribute(element.getAttribute(cursor++));
        }

    }

    private static final class XomElementsIterator extends ReadOnlyIterator<XomElement> {

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
        public XomElement next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return new XomElement(elements.get(cursor++));
        }

    }

}
