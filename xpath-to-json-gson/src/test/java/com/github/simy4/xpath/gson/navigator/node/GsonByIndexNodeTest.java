/*
 * Copyright 2021 Alex Simkin
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
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GsonByIndexNodeTest {

  private final JsonArray jsonArray = new JsonArray();
  private final GsonNode byIndexNode = new GsonByIndexNode(jsonArray, 1, null);

  @BeforeEach
  void setUp() {
    jsonArray.add(1);
    jsonArray.add(2);
    jsonArray.add(3);
  }

  @Test
  void shouldRetrieveElementByIndexOnGet() {
    assertThat(byIndexNode.get()).isEqualTo(new JsonPrimitive(2));
  }

  @Test
  void shouldSetElementByIndexOnSet() {
    byIndexNode.set(new JsonPrimitive(4));

    assertThat(jsonArray)
        .containsExactly(new JsonPrimitive(1), new JsonPrimitive(4), new JsonPrimitive(3));
  }

  @Test
  void shouldRemoveElementByIndexOnSetNull() {
    byIndexNode.set(null);

    assertThat(jsonArray).containsExactly(new JsonPrimitive(1), new JsonPrimitive(3));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldTraverseArrayAttributes() {
    GsonNode parent = new GsonRootNode(jsonArray);

    assertThat((Iterable<GsonNode>) parent.attributes())
        .containsExactlyInAnyOrder(
            new GsonByIndexNode(jsonArray, 0, parent),
            new GsonByIndexNode(jsonArray, 1, parent),
            new GsonByIndexNode(jsonArray, 2, parent));
  }

  @Test
  void shouldTraverseArrayElements() {
    GsonNode parent = new GsonRootNode(jsonArray);

    assertThat((Iterable<?>) parent.elements()).isEmpty();
  }
}
