/*
 * Copyright 2017-2021 Alex Simkin
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
package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.helpers.SerializationHelper;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JDomAttributeTest {

  private JDomAttribute node;

  @BeforeEach
  void setUp() {
    var attribute = new Attribute("attr", "text");
    attribute.setNamespace(Namespace.getNamespace("my", "http://www.example.com/my"));
    node = new JDomAttribute(attribute);
  }

  @Test
  void shouldReturnEmptyListWhenObtainAttributes() {
    assertThat(node.attributes()).isEmpty();
  }

  @Test
  void shouldReturnEmptyListWhenObtainElements() {
    assertThat(node.elements()).isEmpty();
  }

  @Test
  void shouldThrowExceptionWhenAppendAttribute() {
    assertThatThrownBy(() -> node.appendAttribute(new Attribute("attr", "")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldThrowExceptionWhenAppendElement() {
    assertThatThrownBy(() -> node.appendElement(new Element("elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldReturnNodeNameWithNamespaceUri() {
    var result = node.getName();

    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly("http://www.example.com/my", "attr", "my");
  }

  @Test
  void shouldReturnNodeTextContent() {
    assertThat(node.getText()).isEqualTo("text");
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
    // when
    var deserializedNode = SerializationHelper.serializeAndDeserializeBack(node);

    // then
    assertThat(deserializedNode).extracting("name").isEqualTo(node.getName());
  }
}
