package com.github.simy4.xpath.gson.navigator;

import com.github.simy4.xpath.gson.navigator.node.GsonByIndexNode;
import com.github.simy4.xpath.gson.navigator.node.GsonByNameNode;
import com.github.simy4.xpath.gson.navigator.node.GsonNode;
import com.github.simy4.xpath.gson.navigator.node.GsonRootNode;
import com.github.simy4.xpath.navigator.Navigator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonNavigatorTest {

    private final JsonObject json = new JsonObject();
    private final GsonNode root = new GsonRootNode(json);
    private final Navigator<GsonNode> navigator = new GsonNavigator(root);

    @Test
    public void shouldReturnRoot() {
        assertThat(navigator.root()).isEqualTo(root);
    }

    @Test
    public void shouldReturnNullParentForRoot() {
        assertThat(navigator.parentOf(root)).isNull();
    }

    @Test
    public void shouldReturnParentForElementChild() {
        json.add("child", new JsonPrimitive("zero"));
        GsonNode childNode = new GsonByNameNode(json, "child", root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    public void shouldReturnParentForArrayChild() {
        JsonArray json = new JsonArray();
        json.add("zero");
        GsonNode root = new GsonRootNode(json);
        GsonNavigator navigator = new GsonNavigator(root);
        GsonNode childNode = new GsonByIndexNode(json, 0, root);

        assertThat(navigator.parentOf(childNode)).isEqualTo(root);
    }

    @Test
    public void shouldReturnParentForNestedArrayChild() {
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

}