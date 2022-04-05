/*
 * Copyright 2018-2021 Alex Simkin
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
package com.github.simy4.xpath.jackson.navigator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByIndexNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByNameNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonNode;
import com.github.simy4.xpath.navigator.Navigator;

import javax.xml.namespace.QName;

public class JacksonNavigator implements Navigator<JacksonNode> {

  private final JacksonNode json;

  public JacksonNavigator(JacksonNode json) {
    this.json = json;
  }

  @Override
  public JacksonNode root() {
    return json;
  }

  @Override
  public JacksonNode parentOf(JacksonNode node) {
    do {
      node = node.getParent();
    } while (node instanceof JacksonByIndexNode);
    return node;
  }

  @Override
  public Iterable<? extends JacksonNode> elementsOf(JacksonNode parent) {
    return parent.elements();
  }

  @Override
  public Iterable<? extends JacksonNode> attributesOf(JacksonNode parent) {
    return parent.attributes();
  }

  @Override
  public JacksonNode createAttribute(JacksonNode parent, QName attribute)
      throws XmlBuilderException {
    return appendElement(parent, attribute.getLocalPart(), new TextNode(""));
  }

  @Override
  public JacksonNode createElement(JacksonNode parent, QName element) throws XmlBuilderException {
    return appendElement(parent, element.getLocalPart(), new ObjectNode(JsonNodeFactory.instance));
  }

  @Override
  public void setText(JacksonNode node, String text) throws XmlBuilderException {
    final var jsonNode = node.get();
    if (jsonNode.isObject()) {
      ((ObjectNode) jsonNode).set("text", new TextNode(text));
    } else if (jsonNode.isArray()) {
      throw new XmlBuilderException("Unable to set text to JSON array: " + jsonNode);
    } else {
      node.set(new TextNode(text));
    }
  }

  @Override
  public void prependCopy(JacksonNode node) throws XmlBuilderException {
    final var parent = node.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prependcopy to root node " + node.get());
    }
    final var nodeToCopy = node.get();
    final var parentNode = parent.get();
    final JacksonNode elementNode;
    if (parentNode.isObject()) {
      final var parentParent = parent.getParent();
      final var name = node.getName().getLocalPart();
      final var jsonObject = new ObjectNode(JsonNodeFactory.instance);
      final JacksonByIndexNode copyNode;
      if (parentParent != null) {
        final var parentParentNode = parentParent.get();
        if (parentParentNode.isArray()) {
          final var jsonArray = (ArrayNode) parentParentNode;
          copyNode = prependToArray(parentParent, parentNode, jsonArray);
          parent.setParent(
              new JacksonByIndexNode(jsonArray, copyNode.getIndex() + 1, parentParent));
        } else {
          copyNode = prependToNewArray(parent, parentNode);
          node.setParent(
              new JacksonByIndexNode((ArrayNode) parent.get(), copyNode.getIndex() + 1, parent));
        }
      } else {
        copyNode = prependToNewArray(parent, parentNode);
      }
      elementNode = new JacksonByNameNode(jsonObject, name, copyNode);
      copyNode.set(jsonObject);
    } else if (parentNode.isArray()) {
      final var jsonArray = (ArrayNode) parentNode;
      final var copyNode = prependToArray(parent, nodeToCopy, jsonArray);
      node.setParent(new JacksonByIndexNode(jsonArray, copyNode.getIndex() + 1, parent));
      elementNode = copyNode;
    } else {
      throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentNode);
    }
    elementNode.set(nodeToCopy.deepCopy());
  }

  @Override
  public void remove(JacksonNode node) throws XmlBuilderException {
    node.set(null);
  }

  private JacksonNode appendElement(JacksonNode parent, String name, JsonNode newNode) {
    final var parentNode = parent.get();
    final JacksonNode elementNode;
    if (parentNode.isObject()) {
      final var parentObject = (ObjectNode) parentNode;
      if (!parentObject.has(name)) {
        elementNode = new JacksonByNameNode(parentObject, name, parent);
      } else {
        final var parentParent = parent.getParent();
        if (parentParent != null) {
          final var parentParentNode = parentParent.get();
          if (parentParentNode.isArray()) {
            elementNode = appendToArray(parentParent, name, (ArrayNode) parentParentNode);
          } else {
            elementNode = appendToNewArray(parent, name, parentObject);
          }
        } else {
          elementNode = appendToNewArray(parent, name, parentObject);
        }
      }
    } else if (parentNode.isArray()) {
      elementNode = appendToArray(parent, name, (ArrayNode) parentNode);
    } else {
      throw new XmlBuilderException("Unable to create element for primitive node: " + parentNode);
    }
    elementNode.set(newNode);
    return elementNode;
  }

  private JacksonNode appendToNewArray(JacksonNode parent, String name, ObjectNode parentObject) {
    final var jsonArray = new ArrayNode(JsonNodeFactory.instance);
    jsonArray.add(parentObject);
    final var elementNode = appendToArray(parent, name, jsonArray);
    parent.set(jsonArray);
    return elementNode;
  }

  private JacksonNode appendToArray(JacksonNode parent, String name, ArrayNode parentArray) {
    final var jsonObject = new ObjectNode(JsonNodeFactory.instance);
    parentArray.add(jsonObject);
    final var parentObjectNode =
        new JacksonByIndexNode(parentArray, parentArray.size() - 1, parent);
    return new JacksonByNameNode(jsonObject, name, parentObjectNode);
  }

  private JacksonByIndexNode prependToNewArray(JacksonNode parent, JsonNode node) {
    final var jsonArray = new ArrayNode(JsonNodeFactory.instance);
    jsonArray.add(node);
    final var elementNode = prependToArray(parent, node, jsonArray);
    parent.set(jsonArray);
    parent.setParent(new JacksonByIndexNode(jsonArray, 1, parent.getParent()));
    return elementNode;
  }

  @SuppressWarnings("ReferenceEquality")
  private JacksonByIndexNode prependToArray(
      JacksonNode parent, JsonNode nodeToCopy, ArrayNode parentArray) {
    var i = parentArray.size() - 1;
    var arrayNode = parentArray.get(i);
    parentArray.add(arrayNode);
    while (nodeToCopy != arrayNode && i > 0) {
      arrayNode = parentArray.get(i - 1);
      parentArray.set(i, arrayNode);
      i -= 1;
    }
    return new JacksonByIndexNode(parentArray, i, parent);
  }
}
