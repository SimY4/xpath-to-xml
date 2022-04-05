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
package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

abstract class AbstractGsonNode implements GsonNode {

  private GsonNode parent;

  AbstractGsonNode(GsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final GsonNode getParent() {
    return parent;
  }

  @Override
  public final void setParent(GsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final String getText() {
    final var jsonElement = get();
    if (jsonElement.isJsonPrimitive()) {
      return jsonElement.getAsString();
    } else if (jsonElement.isJsonNull()) {
      return "null";
    } else if (jsonElement.isJsonObject()) {
      final var text = jsonElement.getAsJsonObject().get("text");
      if (null != text) {
        if (text.isJsonNull()) {
          return "null";
        } else if (text.isJsonPrimitive()) {
          return text.getAsString();
        }
      }
    }
    return "";
  }

  @Override
  public final Iterable<? extends GsonNode> elements() {
    return () -> traverse(get(), this, false);
  }

  @Override
  public final Iterable<? extends GsonNode> attributes() {
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

    var that = (AbstractGsonNode) o;
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

  private static Iterator<GsonNode> traverse(
      JsonElement jsonElement, GsonNode parent, boolean attribute) {
    if (jsonElement.isJsonObject()) {
      final var jsonObject = jsonElement.getAsJsonObject();
      return new JsonObjectIterator(jsonObject.keySet().iterator(), jsonObject, parent, attribute);
    } else if (jsonElement.isJsonArray()) {
      final var jsonArray = jsonElement.getAsJsonArray();
      return new JsonArrayIterator(jsonArray.iterator(), jsonArray, parent, attribute);
    } else {
      return Collections.emptyIterator();
    }
  }

  private static boolean isAttribute(JsonElement jsonElement) {
    return jsonElement.isJsonPrimitive() || jsonElement.isJsonNull();
  }

  private static final class JsonObjectIterator implements Iterator<GsonNode> {

    private final Iterator<String> keysIterator;
    private final JsonObject parentObject;
    private final GsonNode parent;
    private final boolean attribute;
    private String nextElement;
    private boolean hasNext;

    private JsonObjectIterator(
        Iterator<String> keysIterator,
        JsonObject parentObject,
        GsonNode parent,
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
    public GsonNode next() {
      return new GsonByNameNode(parentObject, nextMatch(), parent);
    }

    private String nextMatch() {
      final var oldMatch = nextElement;
      while (keysIterator.hasNext()) {
        final var next = keysIterator.next();
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

  private static final class JsonArrayIterator implements Iterator<GsonNode> {

    private final Iterator<JsonElement> arrayIterator;
    private final JsonArray parentArray;
    private final GsonNode parent;
    private int index;
    private final boolean attribute;
    private Iterator<GsonNode> current = Collections.<GsonNode>emptyList().iterator();

    private JsonArrayIterator(
        Iterator<JsonElement> arrayIterator,
        JsonArray parentArray,
        GsonNode parent,
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
        final var jsonElement = arrayIterator.next();
        final var arrayElemNode = new GsonByIndexNode(parentArray, index++, parent);
        current =
            isAttribute(jsonElement)
                ? traverseAttributeNode(arrayElemNode)
                : traverse(jsonElement, arrayElemNode, attribute);
      }
      return currentHasNext;
    }

    @Override
    public GsonNode next() {
      return current.next();
    }

    private Iterator<GsonNode> traverseAttributeNode(GsonNode arrayNode) {
      return attribute ? Set.of(arrayNode).iterator() : Collections.emptyIterator();
    }
  }
}
