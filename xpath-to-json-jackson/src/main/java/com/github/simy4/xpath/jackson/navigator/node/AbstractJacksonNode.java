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

import javax.xml.namespace.QName;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
          return Objects.toString(text.asText(), "");
        }
      }
    }
    return "";
  }

  @Override
  public final Stream<JacksonNode> elements() {
    return traverse(get(), this, false);
  }

  @Override
  public final Stream<JacksonNode> attributes() {
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

  static Stream<JacksonNode> traverse(JsonNode jsonNode, JacksonNode parent, boolean attribute) {
    if (jsonNode.isObject()) {
      return StreamSupport.stream(
              Spliterators.spliteratorUnknownSize(
                  jsonNode.fieldNames(),
                  Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.NONNULL),
              false)
          .filter(name -> attribute == isAttribute(jsonNode.get(name)))
          .map(name -> new JacksonByNameNode(QName.valueOf(name), parent));
    } else if (jsonNode.isArray()) {
      return IntStream.range(0, jsonNode.size())
          .mapToObj(jsonNode::get)
          .flatMap(new JsonArrayWrapper(parent, attribute));
    } else {
      return Stream.empty();
    }
  }

  static boolean isAttribute(JsonNode jsonNode) {
    return jsonNode.isValueNode();
  }

  private static final class JsonArrayWrapper implements Function<JsonNode, Stream<JacksonNode>> {

    private final JacksonNode parent;
    private final boolean attribute;
    private int index;

    JsonArrayWrapper(JacksonNode parent, boolean attribute) {
      this.parent = parent;
      this.attribute = attribute;
    }

    @Override
    public Stream<JacksonNode> apply(JsonNode jsonValue) {
      final JacksonNode arrayElemNode = new JacksonByIndexNode(index++, parent);
      return isAttribute(jsonValue)
          ? attribute ? Stream.of(arrayElemNode) : Stream.empty()
          : traverse(jsonValue, arrayElemNode, attribute);
    }
  }
}
