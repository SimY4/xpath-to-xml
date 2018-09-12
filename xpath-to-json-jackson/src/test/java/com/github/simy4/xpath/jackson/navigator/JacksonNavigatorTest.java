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
        JacksonNode childNode = new JacksonByNameNode(json, "child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForArrayChild() {
        ArrayNode json = new ArrayNode(JsonNodeFactory.instance);
        json.add("zero");
        JacksonNode root = new JacksonRootNode(json);
        JacksonNavigator navigator = new JacksonNavigator(root);
        JacksonNode childNode = new JacksonByIndexNode(json, 0, root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    void shouldReturnParentForNestedArrayChild() {
        ArrayNode json = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode child = new ArrayNode(JsonNodeFactory.instance);
        child.add("zero");
        json.add(child);
        JacksonNode root = new JacksonRootNode(json);
        JacksonNavigator navigator = new JacksonNavigator(root);
        JacksonNode array1Node = new JacksonByIndexNode(json, 0, root);
        JacksonNode array2Node = new JacksonByIndexNode(child, 0, array1Node);

        assertThat(navigator.parentOf(array2Node)).isEqualTo(root);
    }

    @Test
    void shouldSetTextForElementChild() {
        navigator.setText(root, "test");

        assertThat(json.get("text")).isEqualTo(new TextNode("test"));
    }

    @Test
    void shouldSetTextForArrayChild() {
        ArrayNode json = new ArrayNode(JsonNodeFactory.instance);
        JacksonNode root = new JacksonRootNode(json);
        JacksonNavigator navigator = new JacksonNavigator(root);

        assertThatThrownBy(() -> navigator.setText(root, "test"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetTextForPrimitiveChild() {
        json.set("child", new TextNode("zero"));
        JacksonNode childNode = new JacksonByNameNode(json, "child", root);
        navigator.setText(childNode, "test");

        assertThat(json.get("child")).isEqualTo(new TextNode("test"));
    }

    @Test
    void shouldCreateElementForElementParent() {
        JacksonNode child = navigator.createElement(root, new QName("child"));

        assertThat(child).isEqualTo(new JacksonByNameNode(json, "child", root));
        assertThat(json.get("child")).isEqualTo(new ObjectNode(JsonNodeFactory.instance));
    }

    @Test
    void shouldCreateElementForArrayParent() {
        ArrayNode json = new ArrayNode(JsonNodeFactory.instance);
        JacksonNode root = new JacksonRootNode(json);
        JacksonNavigator navigator = new JacksonNavigator(root);

        JacksonNode child = navigator.createElement(root, new QName("child"));

        ObjectNode expected = new ObjectNode(JsonNodeFactory.instance);
        expected.set("child", new ObjectNode(JsonNodeFactory.instance));
        assertThat(child).isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 0, root)));
        assertThat(json.get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateElementForPrimitiveParent() {
        json.set("child", new TextNode("zero"));
        JacksonNode childNode = new JacksonByNameNode(json, "child", root);

        assertThatThrownBy(() -> navigator.createElement(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldCreateAttributeForElementParent() {
        JacksonNode child = navigator.createAttribute(root, new QName("child"));

        assertThat(child).isEqualTo(new JacksonByNameNode(json, "child", root));
        assertThat(json.get("child")).isEqualTo(new TextNode(""));
    }

    @Test
    void shouldCreateAttributeForArrayParent() {
        ArrayNode json = new ArrayNode(JsonNodeFactory.instance);
        JacksonNode root = new JacksonRootNode(json);
        JacksonNavigator navigator = new JacksonNavigator(root);

        JacksonNode child = navigator.createAttribute(root, new QName("child"));

        ObjectNode expected = new ObjectNode(JsonNodeFactory.instance);
        expected.set("child", new TextNode(""));
        assertThat(child).isEqualTo(new JacksonByNameNode(expected, "child", new JacksonByIndexNode(json, 0, root)));
        assertThat(json.get(0)).isEqualTo(expected);
    }

    @Test
    void shouldCreateAttributeForPrimitiveParent() {
        json.set("child", new TextNode("zero"));
        JacksonNode childNode = new JacksonByNameNode(json, "child", root);

        assertThatThrownBy(() -> navigator.createAttribute(childNode, new QName("child")))
                .isInstanceOf(XmlBuilderException.class);
    }

}