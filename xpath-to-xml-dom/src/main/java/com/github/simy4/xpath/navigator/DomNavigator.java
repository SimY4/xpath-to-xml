package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Iterator;

final class DomNavigator implements Navigator<Node> {

    private final Document document;
    private final NodeWrapper<Node> xml;

    DomNavigator(Node xml) {
        this.xml = new DomNodeWrapper(xml);
        this.document = xml.getNodeType() == Node.DOCUMENT_NODE ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public NodeWrapper<Node> xml() {
        return xml;
    }

    @Override
    public NodeWrapper<Node> root() {
        return new DomNodeWrapper(document);
    }

    @Override
    @Nullable
    public NodeWrapper<Node> parentOf(NodeWrapper<Node> node) {
        Node parent = node.getWrappedNode().getParentNode();
        return null == parent ? null : new DomNodeWrapper(parent);
    }

    @Override
    public Iterable<NodeWrapper<Node>> elementsOf(final NodeWrapper<Node> parent) {
        return new Iterable<NodeWrapper<Node>>() {
            @Override
            @Nonnull
            public Iterator<NodeWrapper<Node>> iterator() {
                return new DomElementsIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public Iterable<NodeWrapper<Node>> attributesOf(final NodeWrapper<Node> parent) {
        return new Iterable<NodeWrapper<Node>>() {
            @Override
            @Nonnull
            public Iterator<NodeWrapper<Node>> iterator() {
                return new DomAttributesIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public NodeWrapper<Node> createAttribute(QName attribute) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
                return new DomNodeWrapper(document.createAttribute(attribute.getLocalPart()));
            } else {
                return new DomNodeWrapper(document.createAttributeNS(attribute.getNamespaceURI(),
                        attribute.getLocalPart()));
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create attribute: " + attribute, de);
        }
    }

    @Override
    public NodeWrapper<Node> createElement(QName element) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
                return new DomNodeWrapper(document.createElement(element.getLocalPart()));
            } else {
                return new DomNodeWrapper(document.createElementNS(element.getNamespaceURI(), element.getLocalPart()));
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create element: " + element, de);
        }
    }

    @Override
    public NodeWrapper<Node> clone(NodeWrapper<Node> toClone) {
        return new DomNodeWrapper(toClone.getWrappedNode().cloneNode(true));
    }

    @Override
    public void setText(NodeWrapper<Node> node, String text) {
        try {
            node.getWrappedNode().setTextContent(text);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to set text content to " + node, de);
        }
    }

    @Override
    public void append(NodeWrapper<Node> parentNode, NodeWrapper<Node> child) throws XmlBuilderException {
        try {
            parentNode.getWrappedNode().appendChild(child.getWrappedNode());
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to append child " + child + " to " + parentNode, de);
        }
    }

    @Override
    public void prepend(NodeWrapper<Node> nextNode, NodeWrapper<Node> nodeToPrepend) throws XmlBuilderException {
        try {
            final Node parent = nextNode.getWrappedNode().getParentNode();
            if (null == parent) {
                throw new XmlBuilderException("Failed to prepend - no parent found of " + nextNode);
            }
            parent.insertBefore(nodeToPrepend.getWrappedNode(), nextNode.getWrappedNode());
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to prepend node " + nodeToPrepend + " to " + nextNode, de);
        }
    }

    @Override
    public void remove(NodeWrapper<Node> node) {
        try {
            Node wrappedNode = node.getWrappedNode();
            Node parent = wrappedNode.getParentNode();
            parent.removeChild(wrappedNode);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to remove child node " + node, de);
        }
    }

}
