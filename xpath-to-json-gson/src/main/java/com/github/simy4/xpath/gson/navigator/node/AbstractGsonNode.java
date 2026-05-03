/*
 * Copyright 2018-2026 Alex Simkin
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

import javax.xml.namespace.QName;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    final JsonElement jsonElement = get();
    if (jsonElement.isJsonPrimitive()) {
      return jsonElement.getAsString();
    } else if (jsonElement.isJsonNull()) {
      return "null";
    } else if (jsonElement.isJsonObject()) {
      final JsonElement text = jsonElement.getAsJsonObject().get("text");
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
  public final Stream<GsonNode> elements() {
    return traverse(get(), AbstractGsonNode.this, false);
  }

  @Override
  public final Stream<GsonNode> attributes() {
    return traverse(get(), AbstractGsonNode.this, true);
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

    AbstractGsonNode that = (AbstractGsonNode) o;
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

  static Stream<GsonNode> traverse(JsonElement jsonElement, GsonNode parent, boolean attribute) {
    if (jsonElement.isJsonObject()) {
      final JsonObject jsonObject = jsonElement.getAsJsonObject();
      return jsonObject.keySet().stream()
          .filter(name -> attribute == isAttribute(jsonObject.get(name)))
          .map(name -> new GsonByNameNode(QName.valueOf(name), parent));
    } else if (jsonElement.isJsonArray()) {
      final JsonArray jsonArray = jsonElement.getAsJsonArray();
      return IntStream.range(0, jsonArray.size())
          .mapToObj(jsonArray::get)
          .flatMap(new JsonArrayWrapper(parent, attribute));
    } else {
      return Stream.empty();
    }
  }

  static boolean isAttribute(JsonElement jsonElement) {
    return jsonElement.isJsonPrimitive() || jsonElement.isJsonNull();
  }

  private static final class JsonArrayWrapper implements Function<JsonElement, Stream<GsonNode>> {

    private final GsonNode parent;
    private final boolean attribute;
    private int index;

    JsonArrayWrapper(GsonNode parent, boolean attribute) {
      this.parent = parent;
      this.attribute = attribute;
    }

    @Override
    public Stream<GsonNode> apply(JsonElement jsonElement) {
      final GsonNode arrayElemNode = new GsonByIndexNode(index++, parent);
      return isAttribute(jsonElement)
          ? attribute ? Stream.of(arrayElemNode) : Stream.empty()
          : traverse(jsonElement, arrayElemNode, attribute);
    }
  }
}
