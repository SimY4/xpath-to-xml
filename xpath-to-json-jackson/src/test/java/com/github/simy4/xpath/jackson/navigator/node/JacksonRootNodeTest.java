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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.XmlBuilderException;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonRootNodeTest {

  private final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
  private final JacksonNode rootNode = new JacksonRootNode(jsonObject);

  @Test
  void shouldReturnRootName() {
    assertThat(rootNode.getName()).isEqualTo(new QName(JacksonNode.DOCUMENT));
  }

  @Test
  void shouldReturnRootNode() {
    assertThat(rootNode.get()).isSameAs(jsonObject);
  }

  @Test
  void shouldReplaceRootNodeOnSet() {
    ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
    rootNode.set(array);
    assertThat(rootNode.get()).isSameAs(array);
  }

  @Test
  void shouldThrowOnSetNull() {
    assertThatThrownBy(() -> rootNode.set(null)).isInstanceOf(XmlBuilderException.class);
  }
}
