package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Iterator;

final class Dom4jNavigator implements Navigator<org.dom4j.Node> {

    private final Dom4jNode xml;

    Dom4jNavigator(org.dom4j.Node xml) {
        this.xml = new Dom4jNode(xml);
    }

    @Override
    public Dom4jNode xml() {
        return xml;
    }

    @Override
    public Dom4jNode root() {
        return new Dom4jNode(xml.getWrappedNode().getDocument());
    }

    @Override
    @Nullable
    public Dom4jNode parentOf(Node<org.dom4j.Node> node) {
        org.dom4j.Node parent = node.getWrappedNode().getParent();
        return null == parent ? null : new Dom4jNode(parent);
    }

    @Override
    public Iterable<Dom4jNode> elementsOf(final Node<org.dom4j.Node> parent) {
        final org.dom4j.Node parentNode = parent.getWrappedNode();
        switch (parentNode.getNodeType()) {
            case org.dom4j.Node.ELEMENT_NODE:
                return new Iterable<Dom4jNode>() {
                    @Override
                    @Nonnull
                    public Iterator<Dom4jNode> iterator() {
                        return new Dom4jElementsIterator(((Element) parentNode).elementIterator());
                    }
                };
            case org.dom4j.Node.DOCUMENT_NODE:
                final Element root = ((Document) parentNode).getRootElement();
                return null == root ? Collections.<Dom4jNode>emptyList()
                        : Collections.singletonList(new Dom4jNode(root));
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public Iterable<Dom4jNode> attributesOf(final Node<org.dom4j.Node> parent) {
        final org.dom4j.Node parentNode = parent.getWrappedNode();
        if (org.dom4j.Node.ELEMENT_NODE != parentNode.getNodeType()) {
            return Collections.emptyList();
        }
        return new Iterable<Dom4jNode>() {
            @Override
            @Nonnull
            public Iterator<Dom4jNode> iterator() {
                return new Dom4jAttributesIterator(((Element) parentNode).attributeIterator());
            }
        };
    }

    @Override
    public Dom4jNode createAttribute(Node<org.dom4j.Node> parent, QName attribute) throws XmlBuilderException {
        final org.dom4j.Node parentNode = parent.getWrappedNode();
        if (org.dom4j.Node.ELEMENT_NODE != parentNode.getNodeType()) {
            throw new XmlBuilderException("Unable to append attribute to a non-element node " + parent);
        }

        final Element parentElement = (Element) parentNode;
        final org.dom4j.QName attributeName = DocumentHelper.createQName(attribute.getLocalPart(),
                new Namespace(attribute.getPrefix(), attribute.getNamespaceURI()));
        final Attribute attr = DocumentHelper.createAttribute(parentElement, attributeName, "");
        parentElement.attributes().add(attr);
        return new Dom4jNode(attr);
    }

    @Override
    public Dom4jNode createElement(Node<org.dom4j.Node> parent, QName element) throws XmlBuilderException {
        final org.dom4j.Node parentNode = parent.getWrappedNode();
        final org.dom4j.QName elementName = DocumentHelper.createQName(element.getLocalPart(),
                new Namespace(element.getPrefix(), element.getNamespaceURI()));
        switch (parentNode.getNodeType()) {
            case org.dom4j.Node.ELEMENT_NODE:
                return new Dom4jNode(((Element) parentNode).addElement(elementName));
            case org.dom4j.Node.DOCUMENT_NODE:
                return new Dom4jNode(((Document) parentNode).addElement(elementName));
            default:
                throw new XmlBuilderException("Unable to append element to " + parent);
        }
    }

    @Override
    public void setText(Node<org.dom4j.Node> node, String text) {
        try {
            node.getWrappedNode().setText(text);
        } catch (UnsupportedOperationException uoe) {
            throw new XmlBuilderException("Unable to set text content to " + node, uoe);
        }
    }

    @Override
    public void prependCopy(Node<org.dom4j.Node> node) throws XmlBuilderException {
        final org.dom4j.Node wrappedNode = node.getWrappedNode();
        if (org.dom4j.Node.ELEMENT_NODE != wrappedNode.getNodeType()) {
            throw new XmlBuilderException("Unable to copy non-element node " + node);
        }
        final Element parent = wrappedNode.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
        }
        final int prependIndex = parent.indexOf(wrappedNode);
        final Element copiedNode = ((Element) wrappedNode).createCopy();
        parent.elements().add(prependIndex, copiedNode);
    }

    @Override
    public void remove(Node<org.dom4j.Node> node) {
        org.dom4j.Node wrappedNode = node.getWrappedNode();
        Element parent = wrappedNode.getParent();
        if (parent != null) {
            parent.remove(wrappedNode);
        } else {
            throw new XmlBuilderException("Unable to remove node " + node
                    + ". Node either root or in detached state");
        }
    }

}
