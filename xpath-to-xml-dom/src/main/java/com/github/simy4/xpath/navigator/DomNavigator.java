package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;
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
    private final Node<org.w3c.dom.Node> xml;

    DomNavigator(org.w3c.dom.Node xml) {
        this.xml = new DomNode(xml);
        this.document = xml.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public Node<org.w3c.dom.Node> xml() {
        return xml;
    }

    @Override
    public Node<org.w3c.dom.Node> root() {
        return new DomNode(document);
    }

    @Override
    @Nullable
    public Node<org.w3c.dom.Node> parentOf(Node<org.w3c.dom.Node> node) {
        org.w3c.dom.Node parent = node.getWrappedNode().getParentNode();
        return null == parent ? null : new DomNode(parent);
    }

    @Override
    public Iterable<Node<org.w3c.dom.Node>> elementsOf(final Node<org.w3c.dom.Node> parent) {
        return new Iterable<Node<org.w3c.dom.Node>>() {
            @Override
            @Nonnull
            public Iterator<Node<org.w3c.dom.Node>> iterator() {
                return new DomElementsIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public Iterable<Node<org.w3c.dom.Node>> attributesOf(final Node<org.w3c.dom.Node> parent) {
        return new Iterable<Node<org.w3c.dom.Node>>() {
            @Override
            @Nonnull
            public Iterator<Node<org.w3c.dom.Node>> iterator() {
                return new DomAttributesIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public Node<org.w3c.dom.Node> createAttribute(QName attribute) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
                return new DomNode(document.createAttribute(attribute.getLocalPart()));
            } else {
                final Attr attr = document.createAttributeNS(attribute.getNamespaceURI(), attribute.getLocalPart());
                attr.setPrefix(attribute.getPrefix());
                return new DomNode(attr);
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create attribute: " + attribute, de);
        }
    }

    @Override
    public Node<org.w3c.dom.Node> createElement(QName element) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
                return new DomNode(document.createElement(element.getLocalPart()));
            } else {
                final Element elem = document.createElementNS(element.getNamespaceURI(), element.getLocalPart());
                elem.setPrefix(element.getPrefix());
                return new DomNode(elem);
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create element: " + element, de);
        }
    }

    @Override
    public Node<org.w3c.dom.Node> clone(Node<org.w3c.dom.Node> toClone) {
        return new DomNode(toClone.getWrappedNode().cloneNode(true));
    }

    @Override
    public void setText(Node<org.w3c.dom.Node> node, String text) {
        try {
            node.getWrappedNode().setTextContent(text);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to set text content to " + node, de);
        }
    }

    @Override
    public void append(Node<org.w3c.dom.Node> parentNode, Node<org.w3c.dom.Node> child) throws XmlBuilderException {
        try {
            parentNode.getWrappedNode().appendChild(child.getWrappedNode());
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to append child " + child + " to " + parentNode, de);
        }
    }

    @Override
    public void prepend(Node<org.w3c.dom.Node> nextNode, Node<org.w3c.dom.Node> nodeToPrepend)
            throws XmlBuilderException {
        try {
            final org.w3c.dom.Node parent = nextNode.getWrappedNode().getParentNode();
            if (null == parent) {
                throw new XmlBuilderException("Failed to prepend - no parent found of " + nextNode);
            }
            parent.insertBefore(nodeToPrepend.getWrappedNode(), nextNode.getWrappedNode());
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to prepend node " + nodeToPrepend + " to " + nextNode, de);
        }
    }

    @Override
    public void remove(Node<org.w3c.dom.Node> node) {
        try {
            org.w3c.dom.Node wrappedNode = node.getWrappedNode();
            org.w3c.dom.Node parent = wrappedNode.getParentNode();
            parent.removeChild(wrappedNode);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to remove child node " + node, de);
        }
    }

}
