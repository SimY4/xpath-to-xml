package com.github.simy4.xpath.jackson.navigator;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByIndexNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonByNameNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonNode;
import com.github.simy4.xpath.jackson.navigator.node.JacksonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

}