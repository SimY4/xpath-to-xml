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
package com.github.simy4.xpath.jdom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jdom.navigator.node.JDomAttribute;
import com.github.simy4.xpath.jdom.navigator.node.JDomDocument;
import com.github.simy4.xpath.jdom.navigator.node.JDomElement;
import com.github.simy4.xpath.jdom.navigator.node.JDomNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JDomNavigatorTest {

  private final Element parent = new Element("parent");
  private final Document root = new Document(parent);
  private final Element xml = new Element("elem");

  private final Attribute attr1 = new Attribute("attr1", "text");
  private final Attribute attr2 = new Attribute("attr2", "text");
  private final Attribute attr3 = new Attribute("attr3", "text");
  private final Element child1 = new Element("child1");
  private final Element child2 = new Element("child2");
  private final Element child3 = new Element("child3");

  private Navigator<JDomNode> navigator;

  @BeforeEach
  void setUp() {
    parent.addContent(xml);

    xml.addContent(child1);
    xml.addContent(child2);
    xml.addContent(child3);
    xml.setAttribute(attr1);
    xml.setAttribute(attr2);
    xml.setAttribute(attr3);

    navigator = new JDomNavigator(new JDomDocument(root));
  }

  @Test
  void testRootNode() {
    assertThat(navigator.root()).hasFieldOrPropertyWithValue("node", root);
  }

  @Test
  void testParentOfRegularNode() {
    assertThat(navigator.parentOf(new JDomElement(xml)))
        .hasFieldOrPropertyWithValue("node", parent);
  }

  @Test
  void testParentOfRootNode() {
    assertThat(navigator.parentOf(new JDomDocument(root))).isNull();
  }

  @Test
  void testElementsOfDocument() {
    assertThat(navigator.elementsOf(new JDomDocument(root)))
        .extracting("node", Element.class)
        .containsExactly(parent);
  }

  @Test
  void testElementsOfElement() {
    assertThat(navigator.elementsOf(new JDomElement(xml)))
        .extracting("node", Element.class)
        .containsExactly(child1, child2, child3);
  }

  @Test
  void testElementsOfNonElement() {
    assertThat(navigator.elementsOf(new JDomAttribute(new Attribute("attr", "")))).isEmpty();
  }

  @Test
  void testAttributesOf() {
    assertThat(navigator.attributesOf(new JDomElement(xml)))
        .extracting("node", Attribute.class)
        .containsExactly(attr1, attr2, attr3);
  }

  @Test
  void testAttributesOfNonElementNode() {
    assertThat(navigator.attributesOf(new JDomDocument(root))).isEmpty();
  }

  @Test
  void testCreateAttributeSuccess() {
    assertThat(xml.getAttribute("attr")).isNull();
    assertThat(navigator.createAttribute(new JDomElement(xml), new QName("attr"))).isNotNull();
    assertThat(xml.getAttribute("attr")).isNotNull();
  }

  @Test
  void testCreateAttributeFailure() {
    assertThatThrownBy(() -> navigator.createAttribute(new JDomDocument(root), new QName("attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testCreateElementSuccess() {
    assertThat(xml.getChildren("elem")).isEmpty();
    assertThat(navigator.createElement(new JDomElement(xml), new QName("elem"))).isNotNull();
    assertThat(xml.getChildren("elem")).isNotEmpty();
  }

  @Test
  void testCreateElementFailure() {
    assertThatThrownBy(
            () ->
                navigator.createElement(
                    new JDomAttribute(new Attribute("attr", "")), new QName("elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testPrependCopySuccess() {
    navigator.prependCopy(new JDomElement(xml));
    var childElements = parent.getChildren();
    assertThat(childElements).hasSize(2);
  }

  @Test
  void testPrependCopyNoParent() {
    assertThatThrownBy(() -> navigator.prependCopy(new JDomElement(new Element("elem"))))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testPrependCopyNotAnElement() {
    assertThatThrownBy(() -> navigator.prependCopy(new JDomDocument(root)))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testSetTextSuccess() {
    navigator.setText(new JDomAttribute(attr1), "text");
    assertThat(attr1.getValue()).isEqualTo("text");
  }

  @Test
  void testSetTextFailure() {
    assertThatThrownBy(() -> navigator.setText(new JDomDocument(root), "text"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testRemoveSuccess() {
    navigator.remove(new JDomElement(xml));
    assertThat(parent.getChildren()).isEmpty();
  }
}
