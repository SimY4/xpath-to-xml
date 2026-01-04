/*
 * Copyright 2025 Alex Simkin
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
package com.github.simy4.xpath.json.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.json.navigator.node.JsonJsonByIndexNode;
import com.github.simy4.xpath.json.navigator.node.JsonJsonByNameNode;
import com.github.simy4.xpath.json.navigator.node.JsonJsonRootNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonJsonNavigatorTest {
  @Test
  void shouldReturnRoot() {
    var root = new JsonJsonRootNode(new JSONObject());
    var navigator = new JsonJsonNavigator(root);

    assertThat(navigator.root()).isEqualTo(root);
  }

  @Test
  void shouldReturnNullParentForRoot() {
    var root = new JsonJsonRootNode(new JSONObject());
    var navigator = new JsonJsonNavigator(root);

    assertThat(navigator.parentOf(root)).isNull();
  }

  @Test
  void shouldReturnParentForElementChild() {
    var json = new JSONObject(Collections.singletonMap("child", "zero"));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var childNode = new JsonJsonByNameNode(QName.valueOf("child"), root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForArrayChild() {
    var json = new JSONArray(Collections.singleton("zero"));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var childNode = new JsonJsonByIndexNode(0, root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForNestedArrayChild() {
    var child = new JSONArray(Collections.singleton("zero"));
    var json = new JSONArray(Collections.singleton(child));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var array1Node = new JsonJsonByIndexNode(0, root);
    var array2Node = new JsonJsonByIndexNode(0, array1Node);

    assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
  }

  @Test
  void shouldSetTextForElementChild() {
    var json = new JSONObject();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    navigator.setText(root, "test");

    assertThat(json.toMap()).containsEntry("text", "test");
  }

  @Test
  void shouldSetTextForArrayChild() {
    var json = new JSONArray();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    assertThatThrownBy(() -> navigator.setText(root, "test"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldSetTextForPrimitiveChild() {
    var json = new JSONObject(Collections.singletonMap("child", "zero"));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var childNode = new JsonJsonByNameNode(QName.valueOf("child"), root);

    navigator.setText(childNode, "test");

    assertThat(json.toMap()).containsEntry("child", "test");
  }

  @Test
  void shouldCreateElementForElementParent() {
    var json = new JSONObject();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    var child = navigator.createElement(root, QName.valueOf("child"));

    assertThat(child).isEqualTo(new JsonJsonByNameNode(QName.valueOf("child"), root));
    assertThat(json.toMap()).containsEntry("child", Collections.emptyMap());
  }

  @Test
  void shouldCreateElementForNestedObjectInArrayChild() {
    var child = new JSONObject(Collections.singletonMap("child", new JSONObject()));
    var json = new JSONArray(Collections.singleton(child));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var objectNode = new JsonJsonByIndexNode(0, root);

    var newChild = navigator.createElement(objectNode, QName.valueOf("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    assertThat(newChild)
        .isEqualTo(
            new JsonJsonByNameNode(QName.valueOf("child"), new JsonJsonByIndexNode(1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateElementForArrayParent() {
    var json = new JSONArray();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    var child = navigator.createElement(root, QName.valueOf("child"));

    var expected = new JSONObject(Collections.singletonMap("child", new JSONObject()));
    assertThat(child)
        .isEqualTo(
            new JsonJsonByNameNode(QName.valueOf("child"), new JsonJsonByIndexNode(0, root)));
    assertThat(json).first().usingEquals(JsonJsonNavigatorTest::jsonEquals).isEqualTo(expected);
  }

  @Test
  void shouldCreateElementForPrimitiveParent() {
    var json = new JSONObject(Collections.singletonMap("child", "zero"));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var childNode = new JsonJsonByNameNode(QName.valueOf("child"), root);

    assertThatThrownBy(() -> navigator.createElement(childNode, QName.valueOf("child")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldCreateAttributeForElementParent() {
    var json = new JSONObject();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    var child = navigator.createAttribute(root, QName.valueOf("child"));

    assertThat(child).isEqualTo(new JsonJsonByNameNode(QName.valueOf("child"), root));
    assertThat(json.toMap()).containsEntry("child", "");
  }

  @Test
  void shouldCreateAttributeForNestedObjectInArrayChild() {
    var child = new JSONObject(Collections.singletonMap("child", ""));
    var json = new JSONArray(Collections.singleton(child));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);
    var objectNode = new JsonJsonByIndexNode(0, root);

    var newChild = navigator.createAttribute(objectNode, QName.valueOf("child"));

    assertThat(newChild.get()).isNotSameAs(child);
    assertThat(newChild)
        .isEqualTo(
            new JsonJsonByNameNode(QName.valueOf("child"), new JsonJsonByIndexNode(1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateAttributeForArrayParent() {
    var json = new JSONArray();
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    var child = navigator.createAttribute(root, QName.valueOf("child"));

    var expected = new JSONObject(Collections.singletonMap("child", ""));
    assertThat(child)
        .isEqualTo(
            new JsonJsonByNameNode(QName.valueOf("child"), new JsonJsonByIndexNode(0, root)));
    assertThat((Iterable<?>) root.get())
        .first()
        .usingEquals(JsonJsonNavigatorTest::jsonEquals)
        .isEqualTo(expected);
  }

  @Test
  void shouldCreateAttributeForPrimitiveParent() {
    var json = new JSONObject(Collections.singletonMap("child", "zero"));
    var root = new JsonJsonRootNode(json);
    var navigator = new JsonJsonNavigator(root);

    var childNode = new JsonJsonByNameNode(QName.valueOf("child"), root);

    assertThatThrownBy(() -> navigator.createAttribute(childNode, QName.valueOf("child")))
        .isInstanceOf(XmlBuilderException.class);
  }

  private static boolean jsonEquals(Object l, Object r) {
    return ((JSONObject) l).toMap().equals(((JSONObject) r).toMap());
  }
}
