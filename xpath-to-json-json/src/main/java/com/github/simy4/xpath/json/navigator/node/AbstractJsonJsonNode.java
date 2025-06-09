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
package com.github.simy4.xpath.json.navigator.node;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.namespace.QName;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class AbstractJsonJsonNode implements JsonJsonNode {

  private JsonJsonNode parent;

  AbstractJsonJsonNode(JsonJsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final JsonJsonNode getParent() {
    return parent;
  }

  @Override
  public final void setParent(JsonJsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final String getText() {
    final Object jsonValue = get();
    if (jsonValue instanceof JSONObject) {
      Object text = ((JSONObject) jsonValue).opt("text");
      if (text instanceof JSONObject || text instanceof JSONArray) {
        return "";
      } else if (text instanceof String) {
        return (String) text;
      } else if (JSONObject.NULL.equals(text)) {
        return "null";
      } else {
        return text.toString();
      }
    } else if (jsonValue instanceof JSONArray) {
      return "";
    } else if (JSONObject.NULL.equals(jsonValue)) {
      return "null";
    } else {
      return jsonValue.toString();
    }
  }

  @Override
  public Stream<JsonJsonNode> elements() {
    return traverse(get(), this, false);
  }

  @Override
  public Stream<JsonJsonNode> attributes() {
    return traverse(get(), this, true);
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

    AbstractJsonJsonNode that = (AbstractJsonJsonNode) o;
    Object node = get();
    if (node instanceof JSONObject) {
      return ((JSONObject) node).similar(that.get());
    } else if (node instanceof JSONArray) {
      return ((JSONArray) node).similar(that.get());
    } else if (JSONObject.NULL.equals(node)) {
      return JSONObject.NULL.equals(that.get());
    } else {
      return node.equals(that.get());
    }
  }

  @Override
  public int hashCode() {
    Object node = get();
    if (node instanceof JSONObject) {
      return ((JSONObject) node).toMap().hashCode();
    } else if (node instanceof JSONArray) {
      return ((JSONArray) node).toList().hashCode();
    } else {
      return Objects.hashCode(node);
    }
  }

  @Override
  public String toString() {
    return Objects.toString(get(), "???");
  }

  static Stream<JsonJsonNode> traverse(Object jsonValue, JsonJsonNode parent, boolean attribute) {
    if (jsonValue instanceof JSONObject) {
      final JSONObject jsonObject = (JSONObject) jsonValue;
      return jsonObject.keySet().stream()
          .filter(name -> attribute == isAttribute(jsonObject.get(name)))
          .map(name -> new JsonJsonByNameNode(QName.valueOf(name), parent));
    } else if (jsonValue instanceof JSONArray) {
      final JSONArray jsonArray = (JSONArray) jsonValue;
      return IntStream.range(0, jsonArray.length())
          .mapToObj(jsonArray::get)
          .flatMap(new JsonArrayWrapper(parent, attribute));
    } else {
      return Stream.empty();
    }
  }

  static boolean isAttribute(Object jsonValue) {
    return !(jsonValue instanceof JSONObject) && !(jsonValue instanceof JSONArray);
  }

  private static final class JsonArrayWrapper implements Function<Object, Stream<JsonJsonNode>> {

    private final JsonJsonNode parent;
    private final boolean attribute;
    private int index;

    JsonArrayWrapper(JsonJsonNode parent, boolean attribute) {
      this.parent = parent;
      this.attribute = attribute;
    }

    @Override
    public Stream<JsonJsonNode> apply(Object jsonValue) {
      final JsonJsonNode arrayElemNode = new JsonJsonByIndexNode(index++, parent);
      return isAttribute(jsonValue)
          ? attribute ? Stream.of(arrayElemNode) : Stream.empty()
          : traverse(jsonValue, arrayElemNode, attribute);
    }
  }
}
