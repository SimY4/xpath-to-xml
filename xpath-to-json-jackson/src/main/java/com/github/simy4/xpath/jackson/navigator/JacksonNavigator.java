/*
 * Copyright 2018-2025 Alex Simkin
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
import com.fasterxml.jackson.databind.node.NullNode;
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
    return () -> parent.traverse().iterator();
  }

  @Override
  public Iterable<? extends JacksonNode> attributesOf(JacksonNode parent) {
    return elementsOf(parent);
  }

  @Override
  public JacksonNode createAttribute(JacksonNode parent, QName attribute)
      throws XmlBuilderException {
    return appendElement(parent, attribute, new TextNode(""));
  }

  @Override
  public JacksonNode createElement(JacksonNode parent, QName element) throws XmlBuilderException {
    return appendElement(parent, element, new ObjectNode(JsonNodeFactory.instance));
  }

  @Override
  public void setText(JacksonNode node, String text) throws XmlBuilderException {
    final JsonNode jsonNode = node.get();
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
    final JacksonNode parent = node.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prependcopy to root node " + node.get());
    }
    final JsonNode nodeToCopy = node.get();
    final JsonNode parentNode = parent.get();
    final JacksonNode elementNode;
    final JacksonByIndexNode copyNode;
    if (parentNode.isObject()) {
      final JacksonNode parentParent = parent.getParent();
      final QName name = node.getName();
      final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
      if (parentParent != null) {
        final JsonNode parentParentNode = parentParent.get();
        if (parentParentNode.isArray()) {
          final ArrayNode jsonArray = (ArrayNode) parentParentNode;
          copyNode = prependToArray(parentParent, parentNode, jsonArray);
          node.setParent(new JacksonByIndexNode(copyNode.getIndex() + 1, parentParent));
        } else {
          final ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
          arrayNode.add(parentNode);
          parent.set(arrayNode);
          copyNode = prependToArray(parent, parentNode, arrayNode);
          node.setParent(new JacksonByIndexNode(copyNode.getIndex() + 1, parent));
        }
      } else {
        final ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        arrayNode.add(parentNode);
        parent.set(arrayNode);
        copyNode = prependToArray(parent, parentNode, arrayNode);
        node.setParent(new JacksonByIndexNode(copyNode.getIndex() + 1, parent));
      }
      elementNode = new JacksonByNameNode(name, copyNode);
      copyNode.set(jsonObject);
    } else if (parentNode.isArray()) {
      final ArrayNode jsonArray = (ArrayNode) parentNode;
      copyNode = prependToArray(parent, nodeToCopy, jsonArray);
      node.setParent(new JacksonByIndexNode(copyNode.getIndex() + 1, parent));
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

  private JacksonNode appendElement(JacksonNode parent, QName name, JsonNode newNode) {
    final JsonNode parentNode = parent.get();
    final JacksonNode elementNode;
    if (parentNode.isObject()) {
      final ObjectNode parentObject = (ObjectNode) parentNode;
      if (!parentObject.has(name.getLocalPart())) {
        elementNode = new JacksonByNameNode(name, parent);
      } else {
        final JacksonNode parentParent = parent.getParent();
        if (parentParent != null) {
          final JsonNode parentParentNode = parentParent.get();
          if (parentParentNode.isArray()) {
            elementNode = appendToArray(parentParent, name, (ArrayNode) parentParentNode);
          } else {
            final ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            arrayNode.add(parentObject);
            parent.set(arrayNode);
            elementNode = appendToArray(parent, name, arrayNode);
          }
        } else {
          final ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
          arrayNode.add(parentObject);
          parent.set(arrayNode);
          elementNode = appendToArray(parent, name, arrayNode);
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

  private JacksonNode appendToArray(JacksonNode parent, QName name, ArrayNode parentArray) {
    final int index = parentArray.size();
    parentArray.add(new ObjectNode(JsonNodeFactory.instance));
    return new JacksonByNameNode(name, new JacksonByIndexNode(index, parent));
  }

  @SuppressWarnings("ReferenceEquality")
  private JacksonByIndexNode prependToArray(
      JacksonNode parent, JsonNode nodeToCopy, ArrayNode parentArray) {
    int i = parentArray.size() - 1;
    JsonNode arrayNode = parentArray.get(i);
    parentArray.add(arrayNode);
    while (nodeToCopy != arrayNode && i > 0) {
      arrayNode = parentArray.get(i - 1);
      parentArray.set(i, NullNode.instance);
      i -= 1;
    }
    return new JacksonByIndexNode(i, parent);
  }
}
