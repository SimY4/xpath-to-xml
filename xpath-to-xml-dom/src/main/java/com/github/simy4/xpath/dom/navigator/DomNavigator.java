package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DomNavigator implements Navigator<DomNode> {

    private final Document document;

    public DomNavigator(Node xml) {
        this.document = Node.DOCUMENT_NODE == xml.getNodeType() ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public DomNode root() {
        return new DomNode(document);
    }

    @Override
    public DomNode parentOf(DomNode node) {
        final var wrappedNode = node.getNode();
        final var parent = Node.ATTRIBUTE_NODE == wrappedNode.getNodeType() ? ((Attr) wrappedNode).getOwnerElement()
                : wrappedNode.getParentNode();
        return null == parent ? null : new DomNode(parent);
    }

    @Override
    public Iterable<DomNode> elementsOf(DomNode parent) {
        final var firstChild = parent.getNode().getFirstChild();
        return null == firstChild ? Collections.emptyList() : () -> Stream.iterate(firstChild, Node::getNextSibling)
                .takeWhile(Objects::nonNull)
                .filter(node -> Node.ELEMENT_NODE == node.getNodeType())
                .map(DomNode::new)
                .iterator();
    }

    @Override
    public Iterable<DomNode> attributesOf(DomNode parent) {
        final var attributes = parent.getNode().getAttributes();
        return () -> IntStream.range(0, attributes.getLength())
                .mapToObj(i -> new DomNode(attributes.item(i)))
                .iterator();
    }

    @Override
    public DomNode createAttribute(DomNode parent, QName attribute) throws XmlBuilderException {
        final var parentNode = parent.getNode();
        if (Node.ELEMENT_NODE != parentNode.getNodeType()) {
            throw new XmlBuilderException("Unable to append attribute to a non-element node " + parent);
        }

        try {
            Attr attr;
            final var parentElement = (Element) parentNode;
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
        final var wrappedNode = node.getNode();
        final var copiedNode = wrappedNode.cloneNode(true);
        try {
            final var parent = wrappedNode.getParentNode();
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
            final var wrappedNode = node.getNode();
            if (wrappedNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                final var attr = (Attr) wrappedNode;
                final var parent = attr.getOwnerElement();
                if (null == parent) {
                    throw new XmlBuilderException("Unable to remove attribute " + node
                            + ". Node either root or in detached state");
                }
                parent.removeAttributeNode(attr);
            } else {
                final var parent = wrappedNode.getParentNode();
                if (null == parent) {
                    throw new XmlBuilderException("Unable to remove node " + node
                            + ". Node either root or in detached state");
                }
                parent.removeChild(wrappedNode);
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Unable to remove child node " + node, de);
        }
    }

}
