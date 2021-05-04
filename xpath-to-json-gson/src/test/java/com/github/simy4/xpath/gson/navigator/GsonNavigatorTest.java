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
    GsonNode childNode = new GsonByNameNode(json, "child", root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForArrayChild() {
    JsonArray json = new JsonArray();
    json.add("zero");
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);
    GsonNode childNode = new GsonByIndexNode(json, 0, root);

    assertThat(navigator.parentOf(childNode)).isEqualTo(root);
  }

  @Test
  void shouldReturnParentForNestedArrayChild() {
    JsonArray json = new JsonArray();
    JsonArray child = new JsonArray();
    child.add("zero");
    json.add(child);
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);
    GsonNode array1Node = new GsonByIndexNode(json, 0, root);
    GsonNode array2Node = new GsonByIndexNode(child, 0, array1Node);

    assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
  }

  @Test
  void shouldSetTextForElementChild() {
    navigator.setText(root, "test");

    assertThat(json.get("text")).isEqualTo(new JsonPrimitive("test"));
  }

  @Test
  void shouldSetTextForArrayChild() {
    JsonArray json = new JsonArray();
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);

    assertThatThrownBy(() -> navigator.setText(root, "test"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldSetTextForPrimitiveChild() {
    json.add("child", new JsonPrimitive("zero"));
    GsonNode childNode = new GsonByNameNode(json, "child", root);
    navigator.setText(childNode, "test");

    assertThat(json.get("child")).isEqualTo(new JsonPrimitive("test"));
  }

  @Test
  void shouldCreateElementForElementParent() {
    GsonNode child = navigator.createElement(root, new QName("child"));

    assertThat(child).isEqualTo(new GsonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new JsonObject());
  }

  @Test
  void shouldCreateElementForNestedObjectInArrayChild() {
    JsonArray json = new JsonArray();
    JsonObject child = new JsonObject();
    child.add("child", new JsonObject());
    json.add(child);
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);
    GsonNode objectNode = new GsonByIndexNode(json, 0, root);

    GsonNode newChild = navigator.createElement(objectNode, new QName("child"));

    JsonObject expected = new JsonObject();
    expected.add("child", new JsonObject());
    assertThat(newChild)
        .isNotSameAs(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateElementForArrayParent() {
    JsonArray json = new JsonArray();
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);

    GsonNode child = navigator.createElement(root, new QName("child"));

    JsonObject expected = new JsonObject();
    expected.add("child", new JsonObject());
    assertThat(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateElementForPrimitiveParent() {
    json.add("child", new JsonPrimitive("zero"));
    GsonNode childNode = new GsonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldCreateAttributeForElementParent() {
    GsonNode child = navigator.createAttribute(root, new QName("child"));

    assertThat(child).isEqualTo(new GsonByNameNode(json, "child", root));
    assertThat(json.get("child")).isEqualTo(new JsonPrimitive(""));
  }

  @Test
  void shouldCreateAttributeForNestedObjectInArrayChild() {
    JsonArray json = new JsonArray();
    JsonObject child = new JsonObject();
    child.add("child", new JsonPrimitive(""));
    json.add(child);
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);
    GsonNode objectNode = new GsonByIndexNode(json, 0, root);

    GsonNode newChild = navigator.createAttribute(objectNode, new QName("child"));

    JsonObject expected = new JsonObject();
    expected.add("child", new JsonPrimitive(""));
    assertThat(newChild)
        .isNotSameAs(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 1, root)));
    assertThat(objectNode.get()).isSameAs(child);
  }

  @Test
  void shouldCreateAttributeForArrayParent() {
    JsonArray json = new JsonArray();
    GsonNode root = new GsonRootNode(json);
    GsonNavigator navigator = new GsonNavigator(root);

    GsonNode child = navigator.createAttribute(root, new QName("child"));

    JsonObject expected = new JsonObject();
    expected.add("child", new JsonPrimitive(""));
    assertThat(child)
        .isEqualTo(new GsonByNameNode(expected, "child", new GsonByIndexNode(json, 0, root)));
    assertThat(json.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldCreateAttributeForPrimitiveParent() {
    json.add("child", new JsonPrimitive("zero"));
    GsonNode childNode = new GsonByNameNode(json, "child", root);

    assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
        .isInstanceOf(XmlBuilderException.class);
  }
}
