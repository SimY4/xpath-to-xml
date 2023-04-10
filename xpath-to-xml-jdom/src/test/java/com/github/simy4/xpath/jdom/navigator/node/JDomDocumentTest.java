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
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JDomDocumentTest {

  private final Element root = new Element("root");

  private JDomDocument node;

  @BeforeEach
  void setUp() {
    root.addContent("text");
    var document = new Document(root);
    node = new JDomDocument(document);
  }

  @Test
  void shouldReturnEmptyListWhenObtainAttributes() {
    assertThat(node.attributes()).isEmpty();
  }

  @Test
  void shouldReturnSingleRootNodeWhenObtainElements() {
    assertThat(node.elements()).asList().containsExactly(new JDomElement(root));
  }

  @Test
  void shouldThrowExceptionWhenAppendPrev() {
    assertThatThrownBy(() -> node.appendPrev(new JDomElement(new Element("elem"))))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldThrowExceptionWhenAppendAttribute() {
    assertThatThrownBy(() -> node.appendChild(new JDomAttribute(new Attribute("attr", ""))))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldThrowBecauseRootElementShouldAlwaysBePresent() {
    assertThatThrownBy(() -> node.appendChild(new JDomElement(new Element("elem"))))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldThrowExceptionWhenAppendNext() {
    assertThatThrownBy(() -> node.appendNext(new JDomElement(new Element("elem"))))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldReturnDocumentName() {
    assertThat(node.getName()).isEqualTo(new QName(JDomNode.DOCUMENT));
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
