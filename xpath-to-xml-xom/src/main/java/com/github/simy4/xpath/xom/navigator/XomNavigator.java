package com.github.simy4.xpath.xom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.xom.navigator.node.XomDocument;
import com.github.simy4.xpath.xom.navigator.node.XomElement;
import com.github.simy4.xpath.xom.navigator.node.XomNode;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.IllegalAddException;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.XMLException;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

final class XomNavigator implements Navigator<XomNode> {

    private final XomNode xml;

    XomNavigator(XomNode xml) {
        this.xml = xml;
    }

    @Override
    public XomNode xml() {
        return xml;
    }

    @Override
    public XomNode root() {
        return new XomDocument(xml.getNode().getDocument());
    }

    @Override
    @Nullable
    public XomNode parentOf(XomNode node) {
        final ParentNode parent = node.getNode().getParent();
        if (parent instanceof Document) {
            return new XomDocument((Document) parent);
        } else if (parent instanceof Element) {
            return new XomElement((Element) parent);
        } else {
            return null;
        }
    }

    @Override
    public Iterable<? extends XomNode<?>> elementsOf(final XomNode parent) {
        return ((XomNode<?>) parent).elements();
    }

    @Override
    public Iterable<? extends XomNode<?>> attributesOf(final XomNode parent) {
        return ((XomNode<?>) parent).attributes();
    }

    @Override
    public XomNode createAttribute(XomNode parent, QName attribute) throws XmlBuilderException {
        final Attribute attr = new Attribute(attribute.getLocalPart(), "");
        if (!XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
            attr.setNamespace(attr.getNamespacePrefix(), attr.getNamespaceURI());
        }
        return parent.appendAttribute(attr);
    }

    @Override
    public XomNode createElement(XomNode parent, QName element) throws XmlBuilderException {
        final Element elem = new Element(element.getLocalPart(), element.getNamespaceURI());
        elem.setNamespacePrefix(element.getPrefix());
        return parent.appendElement(elem);
    }

    @Override
    public void setText(XomNode node, String text) {
        try {
            node.setValue(text);
        } catch (UnsupportedOperationException uoe) {
            throw new XmlBuilderException("Unable to set text content to " + node, uoe);
        }
    }

    @Override
    public void prependCopy(XomNode node) throws XmlBuilderException {
        final Node wrappedNode = node.getNode();
        if (!(wrappedNode instanceof Element)) {
            throw new XmlBuilderException("Unable to copy non-element node " + node);
        }
        final ParentNode parent = wrappedNode.getParent();
        if (null == parent) {
            throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
        }
        try {
            final int prependIndex = parent.indexOf(wrappedNode);
            final Node copiedNode = wrappedNode.copy();
            parent.insertChild(copiedNode, prependIndex);
        } catch (IllegalAddException iae) {
            throw new XmlBuilderException("Unable to append an copied element to " + parent, iae);
        }
    }

    @Override
    public void remove(XomNode node) throws XmlBuilderException {
        try {
            node.getNode().detach();
        } catch (XMLException xe) {
            throw new XmlBuilderException("Unable to remove node " + node, xe);
        }
    }

}
