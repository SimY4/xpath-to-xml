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
package com.github.simy4.xpath.gson.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.gson.navigator.node.GsonByIndexNode;
import com.github.simy4.xpath.gson.navigator.node.GsonByNameNode;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.gson.navigator.node.GsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GsonNavigatorTest {

  private final JsonObject json = new JsonObject();
  private final GsonNode root = new GsonRootNode(json);
  private final Navigator<GsonNode> navigator = new GsonNavigator(root);

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
    json.add("child", new JsonPrimitive("zero"));
    var childNode = new GsonByNameNode(json, "child", root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForArrayChild() {
    var json = new JsonArray();
    json.add("zero");
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);
    var childNode = new GsonByIndexNode(json, 0, root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForNestedArrayChild() {
    var json = new JsonArray();
    var child = new JsonArray();
    child.add("zero");
    json.add(child);
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);
    var array1Node = new GsonByIndexNode(json, 0, root);
    var array2Node = new GsonByIndexNode(child, 0, array1Node);

    assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
  }

  @Test
  void shouldSetTextForElementChild() {
    navigator.setText(root, "test");

    assertThat(json.get("text")).isEqualTo(new JsonPrimitive("test"));
  }

  @Test
  void shouldSetTextForArrayChild() {
    var json = new JsonArray();
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);

    assertThatThrownBy(() -> navigator.setText(root, "test"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldSetTextForPrimitiveChild() {
    json.add("child", new JsonPrimitive("zero"));
    var childNode = new GsonByNameNode(json, "child", root);
    navigator.setText(childNode, "test");

    assertThat(json.get("child")).isEqualTo(new JsonPrimitive("test"));
  }

  @Test
  void shouldCreateElementForElementParent() {
    var child = navigator.createElement(root, new QName("child"));

    assertThat(child).isEqualTo(new GsonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new JsonObject());
  }

  @Test
  void shouldCreateElementForNestedObjectInArrayChild() {
    var json = new JsonArray();
    var child = new JsonObject();
    child.add("child", new JsonObject());
    json.add(child);
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);
    var objectNode = new GsonByIndexNode(json, 0, root);

    var newChild = navigator.createElement(objectNode, new QName("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    var expected = new JsonObject();
    expected.add("child", new JsonObject());
    assertThat(newChild)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateElementForArrayParent() {
    var json = new JsonArray();
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);

    var child = navigator.createElement(root, new QName("child"));

    var expected = new JsonObject();
    expected.add("child", new JsonObject());
    assertThat(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateElementForPrimitiveParent() {
    json.add("child", new JsonPrimitive("zero"));
    var childNode = new GsonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldCreateAttributeForElementParent() {
    var child = navigator.createAttribute(root, new QName("child"));

    assertThat(child).isEqualTo(new GsonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new JsonPrimitive(""));
  }

  @Test
  void shouldCreateAttributeForNestedObjectInArrayChild() {
    var json = new JsonArray();
    var child = new JsonObject();
    child.add("child", new JsonPrimitive(""));
    json.add(child);
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);
    var objectNode = new GsonByIndexNode(json, 0, root);

    var newChild = navigator.createAttribute(objectNode, new QName("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    var expected = new JsonObject();
    expected.add("child", new JsonPrimitive(""));
    assertThat(newChild)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateAttributeForArrayParent() {
    var json = new JsonArray();
    var root = new GsonRootNode(json);
    var navigator = new GsonNavigator(root);

    var child = navigator.createAttribute(root, new QName("child"));

    var expected = new JsonObject();
    expected.add("child", new JsonPrimitive(""));
    assertThat(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateAttributeForPrimitiveParent() {
    json.add("child", new JsonPrimitive("zero"));
    var childNode = new GsonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }
}
