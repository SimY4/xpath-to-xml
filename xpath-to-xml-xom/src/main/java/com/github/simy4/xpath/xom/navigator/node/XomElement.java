package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.Text;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class XomElement extends AbstractXomNode<Element> {

    public XomElement(Element element) {
        super(element);
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
        return (Iterable<XomElement>) () -> new XomElementsIterator(getNode().getChildElements());
    }

    @Override
    public Iterable<? extends XomNode> attributes() {
        return (Iterable<XomAttribute>) () -> new XomAttributesIterator(getNode());
    }

    @Override
    public XomNode appendAttribute(Attribute attribute) throws XmlBuilderException {
        try {
            getNode().addAttribute(attribute);
            return new XomAttribute(attribute);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an attribute to " + getNode(), iae);
        }
    }

    @Override
    public XomNode appendElement(Element element) throws XmlBuilderException {
        try {
            getNode().appendChild(element);
            return new XomElement(element);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an element to " + getNode(), iae);
        }
    }

    @Override
    public void setText(String text) throws XmlBuilderException {
        try {
            for (int i = 0; i < getNode().getChildCount(); i++) {
                final Node child = getNode().getChild(i);
                if (child instanceof Text) {
                    ((Text) child).setValue(text);
                    return;
                }
            }
            getNode().appendChild(text);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to set value to " + getNode(), iae);
        }
    }

    private static final class XomAttributesIterator implements Iterator<XomAttribute> {

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

    private static final class XomElementsIterator implements Iterator<XomElement> {

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
