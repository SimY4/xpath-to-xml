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
package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jAttribute;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jDocument;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jElement;
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
    Node wrappedNode = parent.getNode();
    if (Node.ELEMENT_NODE != wrappedNode.getNodeType()) {
      throw new XmlBuilderException("Unable to create attribute to a non-element node " + parent);
    }
    final org.dom4j.QName attributeName =
        DocumentHelper.createQName(
            attribute.getLocalPart(),
            new Namespace(attribute.getPrefix(), attribute.getNamespaceURI()));
    return new Dom4jAttribute(
        DocumentHelper.createAttribute((Element) wrappedNode, attributeName, ""));
  }

  @Override
  public Dom4jNode createElement(Dom4jNode parent, QName element) {
    final org.dom4j.QName elementName =
        DocumentHelper.createQName(
            element.getLocalPart(), new Namespace(element.getPrefix(), element.getNamespaceURI()));
    return new Dom4jElement(DocumentHelper.createElement(elementName));
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
  public void appendPrev(Dom4jNode node, Dom4jNode prepend) throws XmlBuilderException {
    final Node wrappedNode = node.getNode();
    final Node nodeToPrepend = prepend.getNode();
    if (Node.ELEMENT_NODE != nodeToPrepend.getNodeType()) {
      throw new XmlBuilderException("Unable to append prev a non-element node " + prepend);
    }
    final Element parent = wrappedNode.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prepend - no parent found of " + node);
    }
    final int prependIndex = parent.indexOf(wrappedNode);
    parent.elements().add(prependIndex, (Element) nodeToPrepend);
  }

  @Override
  public void appendChild(Dom4jNode parent, Dom4jNode node) throws XmlBuilderException {
    parent.appendChild(node);
  }

  @Override
  public void appendNext(Dom4jNode node, Dom4jNode append) throws XmlBuilderException {
    final Node wrappedNode = node.getNode();
    final Node nodeToAppend = append.getNode();
    if (Node.ELEMENT_NODE != nodeToAppend.getNodeType()) {
      throw new XmlBuilderException("Unable to append next a non-element node " + append);
    }
    final Element parent = wrappedNode.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to append - no parent found of " + node);
    }
    parent.elements().add((Element) nodeToAppend);
  }

  @Override
  public void remove(Dom4jNode node) {
    final Node wrappedNode = node.getNode();
    final Element parent = wrappedNode.getParent();
    if (parent != null) {
      parent.remove(wrappedNode);
    } else {
      throw new XmlBuilderException(
          "Unable to remove node " + node + ". Node either root or in detached state");
    }
  }
}
