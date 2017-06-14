package com.github.simy4.xpath.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.DomNodeView;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.Iterator;

final class DomNavigator implements Navigator<Node> {

    private final Document document;
    private final NodeView<Node> xml;

    DomNavigator(Node xml) {
        this.xml = new DomNodeView(xml);
        this.document = xml.getNodeType() == Node.DOCUMENT_NODE ? (Document) xml : xml.getOwnerDocument();
    }

    @Override
    public NodeView<Node> xml() {
        return xml;
    }

    @Override
    public NodeView<Node> root() {
        return new DomNodeView(document);
    }

    @Override
    @Nullable
    public NodeView<Node> parentOf(NodeView<Node> node) {
        Node parent = node.getWrappedNode().getParentNode();
        return null == parent ? null : new DomNodeView(parent);
    }

    @Override
    public Iterable<NodeView<Node>> elementsOf(final NodeView<Node> parent) {
        return new Iterable<NodeView<Node>>() {
            @Override
            @Nonnull
            public Iterator<NodeView<Node>> iterator() {
                return new DomElementsIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public Iterable<NodeView<Node>> attributesOf(final NodeView<Node> parent) {
        return new Iterable<NodeView<Node>>() {
            @Override
            @Nonnull
            public Iterator<NodeView<Node>> iterator() {
                return new DomAttributesIterator(parent.getWrappedNode());
            }
        };
    }

    @Override
    public NodeView<Node> createAttribute(QName attribute) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
                return new DomNodeView(document.createAttribute(attribute.getLocalPart()));
            } else {
                final Attr attr = document.createAttributeNS(attribute.getNamespaceURI(), attribute.getLocalPart());
                attr.setPrefix(attribute.getPrefix());
                return new DomNodeView(attr);
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create attribute: " + attribute, de);
        }
    }

    @Override
    public NodeView<Node> createElement(QName element) throws XmlBuilderException {
        try {
            if (XMLConstants.NULL_NS_URI.equals(element.getNamespaceURI())) {
                return new DomNodeView(document.createElement(element.getLocalPart()));
            } else {
                final Element elem = document.createElementNS(element.getNamespaceURI(), element.getLocalPart());
                elem.setPrefix(element.getPrefix());
                return new DomNodeView(elem);
            }
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to create element: " + element, de);
        }
    }

    @Override
    public NodeView<Node> clone(NodeView<Node> toClone) {
        return new DomNodeView(toClone.getWrappedNode().cloneNode(true));
    }

    @Override
    public void setText(NodeView<Node> node, String text) {
        try {
            node.getWrappedNode().setTextContent(text);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to set text content to " + node, de);
        }
    }

    @Override
    public void append(NodeView<Node> parentNode, NodeView<Node> child) throws XmlBuilderException {
        try {
            parentNode.getWrappedNode().appendChild(child.getWrappedNode());
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to append child " + child + " to " + parentNode, de);
        }
    }

    @Override
    public void prepend(NodeView<Node> nextNode, NodeView<Node> nodeToPrepend) throws XmlBuilderException {
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
    public void remove(NodeView<Node> node) {
        try {
            Node wrappedNode = node.getWrappedNode();
            Node parent = wrappedNode.getParentNode();
            parent.removeChild(wrappedNode);
        } catch (DOMException de) {
            throw new XmlBuilderException("Failed to remove child node " + node, de);
        }
    }

}
