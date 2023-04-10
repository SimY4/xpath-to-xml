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
package com.github.simy4.xpath.jackson.navigator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByIndexNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByNameNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonNavigatorTest {

  private final ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
  private final JacksonNode root = new JacksonRootNode(json);
  private final Navigator<JacksonNode> navigator = new JacksonNavigator(root);

  @Test
  void shouldReturnRoot() {
    assertThat(navigator.root()).isEqualTo(root);
  }

  @Test
  void shouldReturnNullParentForRoot() {
    assertThat(navigator.parentOf(root)).isNull();
  }

  @Test
  void shouldReturnParentForElementChild() {
    json.put("child", "zero");
    var childNode = new JacksonByNameNode(json, "child", root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForArrayChild() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    json.add("zero");
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);
    var childNode = new JacksonByIndexNode(json, 0, root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForNestedArrayChild() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var child = new ArrayNode(JsonNodeFactory.instance);
    child.add("zero");
    json.add(child);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);
    var array1Node = new JacksonByIndexNode(json, 0, root);
    var array2Node = new JacksonByIndexNode(child, 0, array1Node);

    assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
  }

  @Test
  void shouldSetTextForElementChild() {
    navigator.setText(root, "test");

    assertThat(json.get("text")).isEqualTo(new TextNode("test"));
  }

  @Test
  void shouldSetTextForArrayChild() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);

    assertThatThrownBy(() -> navigator.setText(root, "test"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldSetTextForPrimitiveChild() {
    json.set("child", new TextNode("zero"));
    var childNode = new JacksonByNameNode(json, "child", root);
    navigator.setText(childNode, "test");

    assertThat(json.get("child")).isEqualTo(new TextNode("test"));
  }

  @Test
  void shouldCreateElementForElementParent() {
    var child = navigator.createElement(root, new QName("child"));

    assertThat(child).isEqualTo(new JacksonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new ObjectNode(JsonNodeFactory.instance));
  }

  @Test
  void shouldCreateElementForNestedObjectInArrayChild() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var child = new ObjectNode(JsonNodeFactory.instance);
    child.set("child", new ObjectNode(JsonNodeFactory.instance));
    json.add(child);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);
    var objectNode = new JacksonByIndexNode(json, 0, root);

    var newChild = navigator.createElement(objectNode, new QName("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    var expected = new ObjectNode(JsonNodeFactory.instance);
    expected.set("child", new ObjectNode(JsonNodeFactory.instance));
    assertThat(newChild)
        .isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateElementForArrayParent() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);

    var child = navigator.createElement(root, new QName("child"));

    var expected = new ObjectNode(JsonNodeFactory.instance);
    expected.set("child", new ObjectNode(JsonNodeFactory.instance));
    assertThat(child)
        .isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateElementForPrimitiveParent() {
    json.set("child", new TextNode("zero"));
    var childNode = new JacksonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldCreateAttributeForElementParent() {
    var child = navigator.createAttribute(root, new QName("child"));

    assertThat(child).isEqualTo(new JacksonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new TextNode(""));
  }

  @Test
  void shouldCreateAttributeForNestedObjectInArrayChild() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var child = new ObjectNode(JsonNodeFactory.instance);
    child.set("child", new TextNode(""));
    json.add(child);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);
    var objectNode = new JacksonByIndexNode(json, 0, root);

    var newChild = navigator.createAttribute(objectNode, new QName("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    var expected = new ObjectNode(JsonNodeFactory.instance);
    expected.set("child", new TextNode(""));
    assertThat(newChild)
        .isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateAttributeForArrayParent() {
    var json = new ArrayNode(JsonNodeFactory.instance);
    var root = new JacksonRootNode(json);
    var navigator = new JacksonNavigator(root);

    var child = navigator.createAttribute(root, new QName("child"));

    var expected = new ObjectNode(JsonNodeFactory.instance);
    expected.set("child", new TextNode(""));
    assertThat(child)
        .isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateAttributeForPrimitiveParent() {
    json.set("child", new TextNode("zero"));
    var childNode = new JacksonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }
}
