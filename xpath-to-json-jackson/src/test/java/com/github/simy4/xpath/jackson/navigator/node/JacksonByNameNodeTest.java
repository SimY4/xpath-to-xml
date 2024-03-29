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

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class JacksonByNameNodeTest {

  private final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
  private final JacksonNode byNameNode = new JacksonByNameNode(jsonObject, "two", null);

  @BeforeEach
  void setUp() {
    jsonObject.set("one", new IntNode(1));
    jsonObject.set("two", new IntNode(2));
    jsonObject.set("three", new IntNode(3));
  }

  @Test
  void shouldRetrieveElementByIndexOnGet() {
    assertThat(byNameNode.get()).isEqualTo(new IntNode(2));
  }

  @Test
  void shouldSetElementByIndexOnSet() {
    byNameNode.set(new IntNode(4));

    assertThat(jsonObject.fields())
        .toIterable()
        .containsExactly(
            entry("one", new IntNode(1)),
            entry("two", new IntNode(4)),
            entry("three", new IntNode(3)));
  }

  @Test
  void shouldRemoveElementByIndexOnSetNull() {
    byNameNode.set(null);

    assertThat(jsonObject.fields())
        .toIterable()
        .containsExactly(entry("one", new IntNode(1)), entry("three", new IntNode(3)));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldTraverseObjectAttributes() {
    var parent = new JacksonRootNode(jsonObject);

    assertThat((Iterable<JacksonNode>) parent.attributes())
        .containsExactlyInAnyOrder(
            new JacksonByNameNode(jsonObject, "one", parent),
            new JacksonByNameNode(jsonObject, "two", parent),
            new JacksonByNameNode(jsonObject, "three", parent));
  }

  @Test
  void shouldTraverseObjectElements() {
    var parent = new JacksonRootNode(jsonObject);

    assertThat(parent.elements()).isEmpty();
  }
}
