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
package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Dom4JAttributeTest {

  private Attribute attribute =
      DocumentHelper.createAttribute(
          DocumentHelper.createElement(new org.dom4j.QName("parent")),
          new org.dom4j.QName("node"),
          "text");

  private Dom4jAttribute node;

  @BeforeEach
  void setUp() {
    node = new Dom4jAttribute(attribute);
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
  void shouldThrowExceptionWhenCreateAttribute() {
    assertThatThrownBy(() -> node.createAttribute(new org.dom4j.QName("attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldThrowExceptionWhenCreateElement() {
    assertThatThrownBy(() -> node.createElement(new org.dom4j.QName("elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldReturnNodeNameForNamespaceUnawareAttribute() {
    var result = node.getName();

    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
  }

  @Test
  void shouldReturnNodeNameForNamespaceAwareAttribute() {
    attribute =
        DocumentHelper.createAttribute(
            DocumentHelper.createElement(new org.dom4j.QName("parent")),
            new org.dom4j.QName("node", new Namespace("my", "http://www.example.com/my")),
            "text");
    node = new Dom4jAttribute(attribute);

    var result = node.getName();

    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly("http://www.example.com/my", "node", "my");
  }

  @Test
  void shouldReturnNodeTextContent() {
    assertThat(node.getText()).isEqualTo("text");
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
    // when
    Node deserializedNode = SerializationHelper.serializeAndDeserializeBack(node);

    // then
    assertThat(deserializedNode).extracting("name").isEqualTo(node.getName());
  }
}
