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

import jakarta.json.JsonObject;
import jakarta.json.spi.JsonProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class JakartaJsonByNameNodeTest {

  private static final JsonProvider jsonProvider = JsonProvider.provider();

  private final JsonObject jsonObject =
      jsonProvider.createObjectBuilder().add("one", 1).add("two", 2).add("three", 3).build();
  private final JakartaJsonNode rootNode = new JakartaJsonRootNode(jsonObject);
  private final JakartaJsonNode byNameNode = new JakartaJsonByNameNode("two", rootNode);

  @Test
  void shouldRetrieveElementByIndexOnGet() {
    assertThat(byNameNode.get()).isEqualTo(jsonProvider.createValue(2));
  }

  @Test
  void shouldSetElementByIndexOnSet() {
    byNameNode.set(jsonProvider, jsonProvider.createValue(4));

    assertThat(rootNode.get().asJsonObject())
        .containsExactly(
            entry("one", jsonProvider.createValue(1)),
            entry("two", jsonProvider.createValue(4)),
            entry("three", jsonProvider.createValue(3)));
  }

  @Test
  void shouldRemoveElementByIndexOnSetNull() {
    byNameNode.set(jsonProvider, null);

    assertThat(rootNode.get().asJsonObject())
        .containsExactly(
            entry("one", jsonProvider.createValue(1)), entry("three", jsonProvider.createValue(3)));
  }

  @Test
  void shouldTraverseObjectAttributes() {
    JakartaJsonNode parent = new JakartaJsonRootNode(jsonObject);

    assertThat(parent.attributes())
        .containsExactlyInAnyOrder(
            new JakartaJsonByNameNode("one", parent),
            new JakartaJsonByNameNode("two", parent),
            new JakartaJsonByNameNode("three", parent));
  }

  @Test
  void shouldTraverseObjectElements() {
    JakartaJsonNode parent = new JakartaJsonRootNode(jsonObject);

    assertThat(parent.elements()).isEmpty();
  }
}
