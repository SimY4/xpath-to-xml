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
package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

abstract class AbstractJacksonNode implements JacksonNode {

  private JacksonNode parent;

  AbstractJacksonNode(JacksonNode parent) {
    this.parent = parent;
  }

  @Override
  public final JacksonNode getParent() {
    return parent;
  }

  @Override
  public final void setParent(JacksonNode parent) {
    this.parent = parent;
  }

  @Override
  public final String getText() {
    final JsonNode jsonNode = get();
    if (jsonNode.isValueNode()) {
      return jsonNode.asText();
    } else if (jsonNode.isNull()) {
      return "null";
    } else if (jsonNode.isObject()) {
      final JsonNode text = jsonNode.get("text");
      if (text != null) {
        if (text.isNull()) {
          return "null";
        } else if (text.isValueNode()) {
          return text.asText("");
        }
      }
    }
    return "";
  }

  @Override
  public final Iterable<? extends JacksonNode> elements() {
    return () -> traverse(get(), this, false);
  }

  @Override
  public final Iterable<? extends JacksonNode> attributes() {
    return () -> traverse(get(), this, true);
  }

  @Override
  @SuppressWarnings("EqualsGetClass")
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AbstractJacksonNode that = (AbstractJacksonNode) o;
    return get().equals(that.get());
  }

  @Override
  public int hashCode() {
    return get().hashCode();
  }

  @Override
  public String toString() {
    return Objects.toString(get(), "???");
  }

  static Iterator<JacksonNode> traverse(JsonNode jsonNode, JacksonNode parent, boolean attribute) {
    if (jsonNode.isObject()) {
      return new JsonObjectIterator(
          jsonNode.fieldNames(), (ObjectNode) jsonNode, parent, attribute);
    } else if (jsonNode.isArray()) {
      return new JsonArrayIterator(jsonNode.elements(), (ArrayNode) jsonNode, parent, attribute);
    } else {
      return Collections.emptyIterator();
    }
  }

  static boolean isAttribute(JsonNode jsonNode) {
    return jsonNode.isValueNode();
  }

  private static final class JsonObjectIterator implements Iterator<JacksonNode> {

    private final Iterator<String> keysIterator;
    private final ObjectNode parentObject;
    private final JacksonNode parent;
    private final boolean attribute;
    private String nextElement;
    private boolean hasNext;

    JsonObjectIterator(
        Iterator<String> keysIterator,
        ObjectNode parentObject,
        JacksonNode parent,
        boolean attribute) {
      this.keysIterator = keysIterator;
      this.parentObject = parentObject;
      this.parent = parent;
      this.attribute = attribute;
      nextMatch();
    }

    @Override
    public boolean hasNext() {
      return hasNext;
    }

    @Override
    public JacksonNode next() {
      return new JacksonByNameNode(parentObject, nextMatch(), parent);
    }

    private String nextMatch() {
      final String oldMatch = nextElement;
      while (keysIterator.hasNext()) {
        final String next = keysIterator.next();
        if (attribute == isAttribute(parentObject.get(next))) {
          hasNext = true;
          nextElement = next;
          return oldMatch;
        }
      }
      hasNext = false;
      return oldMatch;
    }
  }

  private static final class JsonArrayIterator implements Iterator<JacksonNode> {

    private final Iterator<JsonNode> arrayIterator;
    private final ArrayNode parentArray;
    private final JacksonNode parent;
    private final boolean attribute;
    private int index;
    private Iterator<JacksonNode> current = Collections.emptyIterator();

    JsonArrayIterator(
        Iterator<JsonNode> arrayIterator,
        ArrayNode parentArray,
        JacksonNode parent,
        boolean attribute) {
      this.arrayIterator = arrayIterator;
      this.parentArray = parentArray;
      this.parent = parent;
      this.attribute = attribute;
    }

    @Override
    public boolean hasNext() {
      boolean currentHasNext;
      while (!(currentHasNext = current.hasNext()) && arrayIterator.hasNext()) {
        final JsonNode jsonNode = arrayIterator.next();
        final JacksonNode arrayElemNode = new JacksonByIndexNode(parentArray, index++, parent);
        current =
            isAttribute(jsonNode)
                ? traverseAttributeNode(arrayElemNode)
                : traverse(jsonNode, arrayElemNode, attribute);
      }
      return currentHasNext;
    }

    @Override
    public JacksonNode next() {
      return current.next();
    }

    private Iterator<JacksonNode> traverseAttributeNode(JacksonNode arrayNode) {
      return attribute ? Collections.singleton(arrayNode).iterator() : Collections.emptyIterator();
    }
  }
}
