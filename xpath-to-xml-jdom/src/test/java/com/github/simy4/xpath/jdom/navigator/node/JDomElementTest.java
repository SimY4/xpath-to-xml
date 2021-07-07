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

import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Node;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class JDomElementTest {

  private JDomNode node;
  private final Attribute attr1 = new Attribute("attr1", "text");
  private final Attribute attr2 = new Attribute("attr2", "text");
  private final Attribute attr3 = new Attribute("attr3", "text");
  private final Element child1 = new Element("child1");
  private final Element child2 = new Element("child2");
  private final Element child3 = new Element("child3");

  @BeforeEach
  void setUp() {
    Element element = new Element("elem", "http://www.example.com/my");
    element.setNamespace(Namespace.getNamespace("my", "http://www.example.com/my"));
    element.setAttribute(attr1);
    element.setAttribute(attr2);
    element.setAttribute(attr3);
    element.addContent("text");
    element.addContent(child1);
    element.addContent(child2);
    element.addContent(child3);

    node = new JDomElement(element);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnListOfAttributesWhenObtainAttributes() {
    assertThat((Iterable<JDomNode>) node.attributes())
        .containsExactly(
            new JDomAttribute(attr1), new JDomAttribute(attr2), new JDomAttribute(attr3));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnListOfElementsWhenObtainElements() {
    assertThat((Iterable<JDomNode>) node.elements())
        .containsExactly(new JDomElement(child1), new JDomElement(child2), new JDomElement(child3));
  }

  @Test
  void shouldAppendNewAttributeWhenAppendAttribute() {
    assertThat(node.appendAttribute(new Attribute("attr", ""))).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenAppendElement() {
    Element elem = new Element("elem");
    assertThat(node.appendElement(elem)).isEqualTo(new JDomElement(elem));
  }

  @Test
  void shouldReturnNodeNameWithNamespaceUri() {
    QName result = node.getName();

    assertThat(result)
        .extracting("namespaceURI", "localPart", "prefix")
        .containsExactly("http://www.example.com/my", "elem", "my");
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
