package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jDocument;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;

import javax.xml.namespace.QName;

public final class Dom4jNavigator implements Navigator<Dom4jNode> {

    private final Dom4jDocument xml;

    public Dom4jNavigator(Dom4jDocument xml) {
        this.xml = xml;
    }

    @Override
    public Dom4jNode root() {
        return xml;
    }

    @Override
    public Dom4jNode parentOf(Dom4jNode node) {
        return node.getParent();
    }

    @Override
    public Iterable<? extends Dom4jNode> elementsOf(final Dom4jNode parent) {
        return parent.elements();
    }

    @Override
    public Iterable<? extends Dom4jNode> attributesOf(final Dom4jNode parent) {
        return parent.attributes();
    }

    @Override
    public Dom4jNode createAttribute(Dom4jNode parent, QName attribute) throws XmlBuilderException {
        final var attributeName = DocumentHelper.createQName(attribute.getLocalPart(),
                new Namespace(attribute.getPrefix(), attribute.getNamespaceURI()));
        return parent.createAttribute(attributeName);
    }

    @Override
    public Dom4jNode createElement(Dom4jNode parent, QName element) throws XmlBuilderException {
        final var elementName = DocumentHelper.createQName(element.getLocalPart(),
                new Namespace(element.getPrefix(), element.getNamespaceURI()));
        return parent.createElement(elementName);
    }

    @Override
    public void setText(Dom4jNode node, String text) {
        try {
            node.getNode().setText(text);
        } catch (UnsupportedOperationException uoe) {
            throw new XmlBuilderException("Unable to set text content to " + node, uoe);
        }
    }

    @Override
    public void prependCopy(Dom4jNode node) throws XmlBuilderException {
        final var wrappedNode = node.getNode();
        if (Node.ELEMENT_NODE != wrappedNode.getNodeType()) {
            throw new XmlBuilderException("Unable to copy non-element node " + node);
        }
        final var parent = wrappedNode.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
        }
        final var prependIndex = parent.indexOf(wrappedNode);
        final var copiedNode = ((Element) wrappedNode).createCopy();
        parent.elements().add(prependIndex, copiedNode);
    }

    @Override
    public void remove(Dom4jNode node) {
        final var wrappedNode = node.getNode();
        final var parent = wrappedNode.getParent();
        if (parent != null) {
            parent.remove(wrappedNode);
        } else {
            throw new XmlBuilderException("Unable to remove node " + node
                    + ". Node either root or in detached state");
        }
    }

}
