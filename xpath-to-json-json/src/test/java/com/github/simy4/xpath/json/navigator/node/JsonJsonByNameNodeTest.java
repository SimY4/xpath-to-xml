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

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class JsonJsonByNameNodeTest {

  private final JSONObject jsonObject = new JSONObject(Map.of("one", 1, "two", 2, "three", 3));
  private final JsonJsonNode rootNode = new JsonJsonRootNode(jsonObject);
  private final JsonJsonNode byNameNode = new JsonJsonByNameNode(QName.valueOf("two"), rootNode);

  @Test
  void shouldRetrieveElementByIndexOnGet() {
    assertThat(byNameNode.get()).isEqualTo(2);
  }

  @Test
  void shouldSetElementByIndexOnSet() {
    byNameNode.set(4);

    assertThat(jsonObject.toMap())
        .containsOnly(entry("one", 1), entry("two", 4), entry("three", 3));
  }

  @Test
  void shouldRemoveElementByIndexOnSetNull() {
    byNameNode.set(null);

    assertThat(jsonObject.toMap()).containsOnly(entry("one", 1), entry("three", 3));
  }

  @Test
  void shouldTraverseObjectAttributes() {
    var parent = new JsonJsonRootNode(jsonObject);

    assertThat(parent.attributes())
        .containsExactlyInAnyOrder(
            new JsonJsonByNameNode(QName.valueOf("one"), parent),
            new JsonJsonByNameNode(QName.valueOf("two"), parent),
            new JsonJsonByNameNode(QName.valueOf("three"), parent));
  }

  @Test
  void shouldTraverseObjectElements() {
    var parent = new JsonJsonRootNode(jsonObject);

    assertThat(parent.elements()).isEmpty();
  }
}
