package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Iterator;

public final class DomNavigator implements Navigator<DomNode> {

    private final Document document;

    public DomNavigator(org.w3c.dom.Node xml) {
        this.document = xml.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public DomNode root() {
        return new DomNode(document);
    }

    @Override
    @Nullable
    public DomNode parentOf(DomNode node) {
        org.w3c.dom.Node parent = node.getNode().getParentNode();
        return null == parent ? null : new DomNode(parent);
    }

    @Override
    public Iterable<DomNode> elementsOf(final DomNode parent) {
        return new Iterable<DomNode>() {
            @Override
            @Nonnull
            public Iterator<DomNode> iterator() {
                return new DomElementsIterator(parent.getNode());
            }
        };
    }

    @Override
    public Iterable<DomNode> attributesOf(final DomNode parent) {
        return new Iterable<DomNode>() {
            @Override
            @Nonnull
            public Iterator<DomNode> iterator() {
                return new DomAttributesIterator(parent.getNode());
            }
        };
    }

    @Override
    public DomNode createAttribute(DomNode parent, QName attribute) throws XmlBuilderException {
        final org.w3c.dom.Node parentNode = parent.getNode();
        if (org.w3c.dom.Node.ELEMENT_NODE != parentNode.getNodeType()) {
            throw new XmlBuilderException("Unable to append attribute to a non-element node " + parent);
        }

        try {
            Attr attr;
            final Element parentElement = (Element) parentNode;
            if (XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
                attr = document.createAttribute(attribute.getLocalPart());
            } else {
                attr = document.createAttributeNS(attribute.getNamespaceURI(), attribute.getLocalPart());
                attr.setPrefix(attribute.getPrefix());
            }
            parentElement.setAttributeNode(attr);
            return new DomNode(attr);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to create attribute: " + attribute, de);
        }
    }

    @Override
    public DomNode createElement(DomNode parent, QName element) throws XmlBuilderException {
        try {
            final Element elem;
            if (XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
                elem = document.createElement(element.getLocalPart());
            } else {
                elem = document.createElementNS(element.getNamespaceURI(), element.getLocalPart());
                elem.setPrefix(element.getPrefix());
            }
            return new DomNode(parent.getNode().appendChild(elem));
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to create element: " + element, de);
        }
    }

    @Override
    public void setText(DomNode node, String text) {
        try {
            node.getNode().setTextContent(text);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to set text content to " + node, de);
        }
    }

    @Override
    public void prependCopy(DomNode node) throws XmlBuilderException {
        final org.w3c.dom.Node wrappedNode = node.getNode();
        final org.w3c.dom.Node copiedNode = wrappedNode.cloneNode(true);
        try {
            final org.w3c.dom.Node parent = wrappedNode.getParentNode();
            if (null == parent) {
                throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
            }
            parent.insertBefore(copiedNode, wrappedNode);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to prepend node " + copiedNode + " to " + node, de);
        }
    }

    @Override
    public void remove(DomNode node) {
        try {
            org.w3c.dom.Node wrappedNode = node.getNode();
            org.w3c.dom.Node parent = wrappedNode.getParentNode();
            if (parent != null) {
                parent.removeChild(wrappedNode);
            } else {
                throw new XmlBuilderException("Unable to remove node " + node
                        + ". Node either root or in detached state");
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to remove child node " + node, de);
        }
    }

}
