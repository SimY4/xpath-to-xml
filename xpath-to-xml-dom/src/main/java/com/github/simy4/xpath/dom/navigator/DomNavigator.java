package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Iterator;

final class DomNavigator implements Navigator<org.w3c.dom.Node> {

    private final Document document;
    private final DomNode xml;

    DomNavigator(org.w3c.dom.Node xml) {
        this.xml = new DomNode(xml);
        this.document = xml.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public DomNode xml() {
        return xml;
    }

    @Override
    public DomNode root() {
        return new DomNode(document);
    }

    @Override
    @Nullable
    public DomNode parentOf(Node<org.w3c.dom.Node> node) {
        org.w3c.dom.Node parent = node.getWrappedNode().getParentNode();
        return null == parent ? null : new DomNode(parent);
    }

    @Override
    public Iterable<DomNode> elementsOf(final Node<org.w3c.dom.Node> parent) {
        return new Iterable<DomNode>() {
            @Override
            @Nonnull
            public Iterator<DomNode> iterator() {
                return new DomElementsIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public Iterable<DomNode> attributesOf(final Node<org.w3c.dom.Node> parent) {
        return new Iterable<DomNode>() {
            @Override
            @Nonnull
            public Iterator<DomNode> iterator() {
                return new DomAttributesIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public DomNode createAttribute(Node<org.w3c.dom.Node> parent, QName attribute) throws XmlBuilderException {
        try {
            final Attr attr;
            if (XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
                attr = document.createAttribute(attribute.getLocalPart());
            } else {
                attr = document.createAttributeNS(attribute.getNamespaceURI(), attribute.getLocalPart());
                attr.setPrefix(attribute.getPrefix());
            }
            org.w3c.dom.Node parentNode = parent.getWrappedNode();
            if (org.w3c.dom.Node.ELEMENT_NODE == parentNode.getNodeType()) {
                ((Element) parentNode).setAttributeNode(attr);
            } else {
                throw new XmlBuilderException("Unable to append attribute " + attr + " to a non-element node "
                        + parentNode);
            }
            return new DomNode(attr);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to create attribute: " + attribute, de);
        }
    }

    @Override
    public DomNode createElement(Node<org.w3c.dom.Node> parent, QName element) throws XmlBuilderException {
        try {
            final Element elem;
            if (XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
                elem = document.createElement(element.getLocalPart());
            } else {
                elem = document.createElementNS(element.getNamespaceURI(), element.getLocalPart());
                elem.setPrefix(element.getPrefix());
            }
            parent.getWrappedNode().appendChild(elem);
            return new DomNode(elem);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to create element: " + element, de);
        }
    }

    @Override
    public void setText(Node<org.w3c.dom.Node> node, String text) {
        try {
            node.getWrappedNode().setTextContent(text);
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to set text content to " + node, de);
        }
    }

    @Override
    public void prependCopy(Node<org.w3c.dom.Node> node) throws XmlBuilderException {
        final org.w3c.dom.Node wrappedNode = node.getWrappedNode();
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
    public void remove(Node<org.w3c.dom.Node> node) {
        try {
            org.w3c.dom.Node wrappedNode = node.getWrappedNode();
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
