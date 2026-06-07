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
package com.github.simy4.xpath.json.navigator.node;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import javax.xml.namespace.QName;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

abstract class AbstractJakartaJsonNode implements JakartaJsonNode {

  private JakartaJsonNode parent;

  AbstractJakartaJsonNode(JakartaJsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final JakartaJsonNode getParent() {
    return parent;
  }

  @Override
  public final void setParent(JakartaJsonNode parent) {
    this.parent = parent;
  }

  @Override
  public final String getText() {
    final JsonValue jsonValue = get();
    switch (jsonValue.getValueType()) {
      case OBJECT:
        JsonValue text = jsonValue.asJsonObject().get("text");
        if (null != text) {
          switch (text.getValueType()) {
            case OBJECT:
            case ARRAY:
              return "";
            case NULL:
              return "null";
            case STRING:
              return ((JsonString) text).getString();
            default:
              return text.toString();
          }
        }
        return "";
      case ARRAY:
        return "";
      case STRING:
        return ((JsonString) jsonValue).getString();
      case NULL:
        return "null";
      default:
        return jsonValue.toString();
    }
  }

  @Override
  public Stream<JakartaJsonNode> traverse() {
    return traverse(get(), this);
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

    AbstractJakartaJsonNode that = (AbstractJakartaJsonNode) o;
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

  static Stream<JakartaJsonNode> traverse(JsonValue jsonValue, JakartaJsonNode parent) {
    switch (jsonValue.getValueType()) {
      case OBJECT:
        final JsonObject jsonObject = jsonValue.asJsonObject();
        return jsonObject.keySet().stream()
            .map(name -> new JakartaJsonByNameNode(QName.valueOf(name), parent));
      case ARRAY:
        return jsonValue.asJsonArray().stream().flatMap(new JsonArrayWrapper(parent));
      default:
        return Stream.empty();
    }
  }

  private static final class JsonArrayWrapper
      implements Function<JsonValue, Stream<JakartaJsonNode>> {

    private final JakartaJsonNode parent;
    private int index;

    JsonArrayWrapper(JakartaJsonNode parent) {
      this.parent = parent;
    }

    @Override
    public Stream<JakartaJsonNode> apply(JsonValue jsonValue) {
      final JsonValue.ValueType valueType = jsonValue.getValueType();
      final JakartaJsonNode arrayElemNode = new JakartaJsonByIndexNode(index++, parent);
      return JsonValue.ValueType.OBJECT != valueType && JsonValue.ValueType.ARRAY != valueType
          ? Stream.of(arrayElemNode)
          : traverse(jsonValue, arrayElemNode);
    }
  }
}
