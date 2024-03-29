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
package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JakartaJsonNode;
import com.github.simy4.xpath.navigator.Navigator;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import javax.xml.namespace.QName;

public class JakartaJsonNavigator implements Navigator<JakartaJsonNode> {

  private final JsonProvider jsonProvider;
  private final JakartaJsonNode json;

  public JakartaJsonNavigator(JsonProvider jsonProvider, JakartaJsonNode json) {
    this.jsonProvider = jsonProvider;
    this.json = json;
  }

  @Override
  public JakartaJsonNode root() {
    return json;
  }

  @Override
  public JakartaJsonNode parentOf(JakartaJsonNode node) {
    do {
      node = node.getParent();
    } while (node instanceof JakartaJsonByIndexNode);
    return node;
  }

  @Override
  public Iterable<? extends JakartaJsonNode> elementsOf(JakartaJsonNode parent) {
    return () -> parent.elements().iterator();
  }

  @Override
  public Iterable<? extends JakartaJsonNode> attributesOf(JakartaJsonNode parent) {
    return () -> parent.attributes().iterator();
  }

  @Override
  public JakartaJsonNode createAttribute(JakartaJsonNode parent, QName attribute)
      throws XmlBuilderException {
    return appendElement(parent, attribute.getLocalPart(), jsonProvider.createValue(""));
  }

  @Override
  public JakartaJsonNode createElement(JakartaJsonNode parent, QName element)
      throws XmlBuilderException {
    return appendElement(parent, element.getLocalPart(), JsonValue.EMPTY_JSON_OBJECT);
  }

  @Override
  public void setText(JakartaJsonNode node, String text) throws XmlBuilderException {
    final JsonString jsonText = jsonProvider.createValue(text);
    JsonValue jsonValue = node.get();
    switch (jsonValue.getValueType()) {
      case OBJECT:
        final JsonObject jsonObject = jsonValue.asJsonObject();
        jsonValue =
            jsonProvider
                .createObjectBuilder(jsonObject)
                .add("text", jsonProvider.createValue(text))
                .build();
        break;
      case ARRAY:
        throw new XmlBuilderException("Unable to set text to JSON array: " + jsonValue);
      default:
        jsonValue = jsonText;
        break;
    }
    node.set(jsonProvider, jsonValue);
  }

  @Override
  public void prependCopy(JakartaJsonNode node) throws XmlBuilderException {
    final JakartaJsonNode parent = node.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prepend copy to root node " + node.get());
    }
    final JsonValue valueToCopy = node.get();
    final JsonValue parentValue = parent.get();
    final JakartaJsonNode elementNode;
    final JakartaJsonByIndexNode copyNode;
    switch (parentValue.getValueType()) {
      case OBJECT:
        final JakartaJsonNode parentParent = parent.getParent();
        final String name = node.getName().getLocalPart();
        final JsonObject jsonObject = JsonValue.EMPTY_JSON_OBJECT;
        if (parentParent != null) {
          final JsonValue parentParentValue = parentParent.get();
          if (JsonValue.ValueType.ARRAY == parentParentValue.getValueType()) {
            final JsonArray jsonArray = parentParentValue.asJsonArray();
            copyNode = prependToArray(parentParent, parentValue, jsonArray);
            node.setParent(new JakartaJsonByIndexNode(copyNode.getIndex() + 1, parentParent));
          } else {
            copyNode = prependToNewArray(parent, parentValue);
            node.setParent(new JakartaJsonByIndexNode(copyNode.getIndex() + 1, parent));
          }
        } else {
          copyNode = prependToNewArray(parent, parentValue);
        }
        elementNode = new JakartaJsonByNameNode(name, copyNode);
        copyNode.set(jsonProvider, jsonObject);
        break;
      case ARRAY:
        final JsonArray jsonArray = parentValue.asJsonArray();
        copyNode = prependToArray(parent, valueToCopy, jsonArray);
        node.setParent(new JakartaJsonByIndexNode(copyNode.getIndex() + 1, parent));
        elementNode = copyNode;
        break;
      default:
        throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentValue);
    }
    elementNode.set(jsonProvider, valueToCopy);
  }

  @Override
  public void remove(JakartaJsonNode node) throws XmlBuilderException {
    node.set(jsonProvider, null);
  }

  private JakartaJsonNode appendElement(JakartaJsonNode parent, String name, JsonValue newValue) {
    final JsonValue parentValue = parent.get();
    final JakartaJsonNode elementNode;
    switch (parentValue.getValueType()) {
      case OBJECT:
        final JsonObject parentObject = parentValue.asJsonObject();
        if (!parentObject.containsKey(name)) {
          elementNode = new JakartaJsonByNameNode(name, parent);
        } else {
          final JakartaJsonNode parentParent = parent.getParent();
          if (parentParent != null) {
            final JsonValue parentParentValue = parentParent.get();
            if (JsonValue.ValueType.ARRAY == parentParentValue.getValueType()) {
              elementNode = appendToArray(parentParent, name, parentParentValue.asJsonArray());
            } else {
              elementNode = appendToNewArray(parent, name, parentObject);
            }
          } else {
            elementNode = appendToNewArray(parent, name, parentObject);
          }
        }
        break;
      case ARRAY:
        elementNode = appendToArray(parent, name, parentValue.asJsonArray());
        break;
      default:
        throw new XmlBuilderException(
            "Unable to create element for primitive node: " + parentValue);
    }
    elementNode.set(jsonProvider, newValue);
    return elementNode;
  }

  private JakartaJsonNode appendToNewArray(
      JakartaJsonNode parent, String name, JsonObject parentObject) {
    final JsonArray jsonArray = jsonProvider.createArrayBuilder().add(parentObject).build();
    return appendToArray(parent, name, jsonArray);
  }

  private JakartaJsonNode appendToArray(
      JakartaJsonNode parent, String name, JsonArray parentArray) {
    final int index = parentArray.size();
    parentArray =
        jsonProvider.createArrayBuilder(parentArray).add(JsonValue.EMPTY_JSON_OBJECT).build();
    parent.set(jsonProvider, parentArray);
    return new JakartaJsonByNameNode(name, new JakartaJsonByIndexNode(index, parent));
  }

  private JakartaJsonByIndexNode prependToNewArray(JakartaJsonNode parent, JsonValue valueToCopy) {
    final JsonArray jsonArray = jsonProvider.createArrayBuilder().add(valueToCopy).build();
    return prependToArray(parent, valueToCopy, jsonArray);
  }

  private JakartaJsonByIndexNode prependToArray(
      JakartaJsonNode parent, JsonValue valueToCopy, JsonArray parentArray) {
    final int index = parentArray.indexOf(valueToCopy);
    parentArray = jsonProvider.createArrayBuilder(parentArray).add(index, valueToCopy).build();
    parent.set(jsonProvider, parentArray);
    return new JakartaJsonByIndexNode(index, parent);
  }
}
