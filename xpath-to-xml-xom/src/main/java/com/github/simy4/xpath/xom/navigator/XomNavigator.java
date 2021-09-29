/*
 * Copyright 2017-2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.xom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.xom.navigator.node.XomAttribute;
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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public final class XomNavigator implements Navigator<XomNode> {

  private final XomDocument xml;

  public XomNavigator(XomDocument xml) {
    this.xml = xml;
  }

  @Override
  public XomNode root() {
    return xml;
  }

  @Override
  public XomNode parentOf(XomNode node) {
    final ParentNode parent = node.getNode().getParent();
    return null == parent
        ? null
        : parent instanceof Element
            ? new XomElement((Element) parent)
            : new XomDocument((Document) parent);
  }

  @Override
  public Iterable<? extends XomNode> elementsOf(final XomNode parent) {
    return parent.elements();
  }

  @Override
  public Iterable<? extends XomNode> attributesOf(final XomNode parent) {
    return parent.attributes();
  }

  @Override
  public XomNode createAttribute(XomNode parent, QName attribute) {
    final Attribute attr = new Attribute(attribute.getLocalPart(), "");
    if (!XMLConstants.NULL_NS_URI.equals(attribute.getNamespaceURI())) {
      attr.setNamespace(attribute.getPrefix(), attribute.getNamespaceURI());
    }
    return new XomAttribute(attr);
  }

  @Override
  public XomNode createElement(XomNode parent, QName element) {
    final Element elem = new Element(element.getLocalPart(), element.getNamespaceURI());
    elem.setNamespacePrefix(element.getPrefix());
    return new XomElement(elem);
  }

  @Override
  public void setText(XomNode node, String text) {
    try {
      node.setText(text);
    } catch (UnsupportedOperationException uoe) {
      throw new XmlBuilderException("Unable to set text content to " + node, uoe);
    }
  }

  @Override
  public void appendPrev(XomNode node, XomNode prepend) throws XmlBuilderException {
    final Node wrappedNode = node.getNode();
    final Node nodeToPrepend = prepend.getNode();
    final ParentNode parent = wrappedNode.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
    }
    try {
      final int prependIndex = parent.indexOf(wrappedNode);
      parent.insertChild(nodeToPrepend, prependIndex);
    } catch (IllegalAddException iae) {
      throw new XmlBuilderException("Unable to append an copied element to " + parent, iae);
    }
  }

  @Override
  public void appendChild(XomNode parent, XomNode node) throws XmlBuilderException {
    parent.appendChild(node);
  }

  @Override
  public void appendNext(XomNode node, XomNode append) throws XmlBuilderException {
    final Node wrappedNode = node.getNode();
    final Node nodeToAppend = append.getNode();
    final ParentNode parent = wrappedNode.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to append - no parent found of " + node);
    }
    try {
      final int prependIndex = parent.indexOf(wrappedNode);
      parent.insertChild(nodeToAppend, prependIndex);
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
