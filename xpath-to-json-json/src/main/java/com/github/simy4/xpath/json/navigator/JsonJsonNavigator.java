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
import com.github.simy4.xpath.json.navigator.node.JsonJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JsonJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JsonJsonNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.namespace.QName;

import java.util.Collections;

public class JsonJsonNavigator implements Navigator<JsonJsonNode> {

  private final JsonJsonNode json;

  public JsonJsonNavigator(JsonJsonNode json) {
    this.json = json;
  }

  @Override
  public JsonJsonNode root() {
    return json;
  }

  @Override
  public JsonJsonNode parentOf(JsonJsonNode node) {
    do {
      node = node.getParent();
    } while (node instanceof JsonJsonByIndexNode);
    return node;
  }

  @Override
  public Iterable<? extends JsonJsonNode> elementsOf(JsonJsonNode parent) {
    return () -> parent.elements().iterator();
  }

  @Override
  public Iterable<? extends JsonJsonNode> attributesOf(JsonJsonNode parent) {
    return () -> parent.attributes().iterator();
  }

  @Override
  public JsonJsonNode createAttribute(JsonJsonNode parent, QName attribute)
      throws XmlBuilderException {
    return appendElement(parent, attribute, "");
  }

  @Override
  public JsonJsonNode createElement(JsonJsonNode parent, QName element) throws XmlBuilderException {
    return appendElement(parent, element, new JSONObject());
  }

  @Override
  public void setText(JsonJsonNode node, String text) throws XmlBuilderException {
    Object jsonValue = node.get();
    if (jsonValue instanceof JSONObject) {
      jsonValue = ((JSONObject) jsonValue).put("text", text);
    } else if (jsonValue instanceof JSONArray) {
      throw new XmlBuilderException("Unable to set text to JSON array: " + jsonValue);
    } else {
      jsonValue = text;
    }
    node.set(jsonValue);
  }

  @Override
  public void prependCopy(JsonJsonNode node) throws XmlBuilderException {
    final JsonJsonNode parent = node.getParent();
    if (null == parent) {
      throw new XmlBuilderException("Unable to prepend copy to root node " + node.get());
    }
    final Object valueToCopy = node.get();
    final Object parentValue = parent.get();
    final JsonJsonNode elementNode;
    final JsonJsonByIndexNode copyNode;
    if (parentValue instanceof JSONObject) {
      final JsonJsonNode parentParent = parent.getParent();
      final QName name = node.getName();
      final JSONObject jsonObject = new JSONObject();
      if (parentParent != null) {
        final Object parentParentValue = parentParent.get();
        if (parentParentValue instanceof JSONArray) {
          copyNode = prependToArray(parentParent, parentValue, (JSONArray) parentParentValue);
          node.setParent(new JsonJsonByIndexNode(copyNode.getIndex() + 1, parentParent));
        } else {
          final JSONArray jsonArray = new JSONArray(Collections.singleton(parentValue));
          parent.set(jsonArray);
          copyNode = prependToArray(parent, parentValue, jsonArray);
          node.setParent(new JsonJsonByIndexNode(1, parent));
        }
      } else {
        final JSONArray jsonArray = new JSONArray(Collections.singleton(parentValue));
        parent.set(jsonArray);
        copyNode = prependToArray(parent, parentValue, jsonArray);
        node.setParent(new JsonJsonByIndexNode(1, parent));
      }
      elementNode = new JsonJsonByNameNode(name, copyNode);
      copyNode.set(jsonObject);
    } else if (parentValue instanceof JSONArray) {
      copyNode = prependToArray(parent, valueToCopy, (JSONArray) parentValue);
      node.setParent(new JsonJsonByIndexNode(copyNode.getIndex() + 1, parent));
      elementNode = copyNode;
    } else {
      throw new XmlBuilderException("Unable to prepend copy to primitive node: " + parentValue);
    }
    elementNode.set(copy(valueToCopy));
  }

  @Override
  public void remove(JsonJsonNode node) throws XmlBuilderException {
    node.set(null);
  }

  private JsonJsonNode appendElement(JsonJsonNode parent, QName name, Object newValue) {
    final Object parentValue = parent.get();
    final JsonJsonNode elementNode;
    if (parentValue instanceof JSONObject) {
      final JSONObject parentObject = (JSONObject) parentValue;
      if (!parentObject.has(name.getLocalPart())) {
        elementNode = new JsonJsonByNameNode(name, parent);
      } else {
        final JsonJsonNode parentParent = parent.getParent();
        if (parentParent != null) {
          final Object parentParentValue = parentParent.get();
          if (parentParentValue instanceof JSONArray) {
            elementNode = appendToArray(parentParent, name, (JSONArray) parentParentValue);
          } else {
            final JSONArray jsonArray = new JSONArray(Collections.singleton(parentObject));
            parent.set(jsonArray);
            elementNode = appendToArray(parent, name, jsonArray);
          }
        } else {
          final JSONArray jsonArray = new JSONArray(Collections.singleton(parentObject));
          parent.set(jsonArray);
          elementNode = appendToArray(parent, name, jsonArray);
        }
      }
    } else if (parentValue instanceof JSONArray) {
      elementNode = appendToArray(parent, name, (JSONArray) parentValue);
    } else {
      throw new XmlBuilderException("Unable to create element for primitive node: " + parentValue);
    }
    elementNode.set(newValue);
    return elementNode;
  }

  private JsonJsonNode appendToArray(JsonJsonNode parent, QName name, JSONArray parentArray) {
    final int index = parentArray.length();
    parentArray.put(new JSONObject());
    return new JsonJsonByNameNode(name, new JsonJsonByIndexNode(index, parent));
  }

  @SuppressWarnings("ReferenceEquality")
  private JsonJsonByIndexNode prependToArray(
      JsonJsonNode parent, Object valueToCopy, JSONArray parentArray) {
    int i = parentArray.length() - 1;
    Object arrayElement = parentArray.get(i);
    parentArray.put(arrayElement);
    while (valueToCopy != arrayElement && i > 0) {
      arrayElement = parentArray.get(i - 1);
      parentArray.put(i, JSONObject.NULL);
      i -= 1;
    }
    return new JsonJsonByIndexNode(i, parent);
  }

  private Object copy(Object toCopy) {
    if (toCopy instanceof JSONObject) {
      return new JSONObject(((JSONObject) toCopy).toMap());
    } else if (toCopy instanceof JSONArray) {
      return new JSONArray(((JSONArray) toCopy).toList());
    } else {
      return toCopy;
    }
  }
}
