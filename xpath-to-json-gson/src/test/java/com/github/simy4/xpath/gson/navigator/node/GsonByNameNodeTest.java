package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonByNameNodeTest {

    private final JsonObject jsonObject = new JsonObject();
    private final GsonNode byNameNode = new GsonByNameNode(jsonObject, "two", null);

    @Before
    public void setUp() {
        jsonObject.addProperty("one", 1);
        jsonObject.addProperty("two", 2);
        jsonObject.addProperty("three", 3);
    }

    @Test
    public void shouldRetrieveElementByIndexOnGet() {
        assertThat(byNameNode.get()).isEqualTo(new JsonPrimitive(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetElementByIndexOnSet() {
        byNameNode.set(new JsonPrimitive(4));

        assertThat(jsonObject.entrySet()).containsExactly(
                new AbstractMap.SimpleEntry<String, JsonElement>("one", new JsonPrimitive(1)),
                new AbstractMap.SimpleEntry<String, JsonElement>("two", new JsonPrimitive(4)),
                new AbstractMap.SimpleEntry<String, JsonElement>("three", new JsonPrimitive(3))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRemoveElementByIndexOnRemove() {
        byNameNode.remove();

        assertThat(jsonObject.entrySet()).containsExactly(
                new AbstractMap.SimpleEntry<String, JsonElement>("one", new JsonPrimitive(1)),
                new AbstractMap.SimpleEntry<String, JsonElement>("three", new JsonPrimitive(3))
        );
    }

    @Test
    public void shouldTraverseObject() {
        GsonNode parent = new GsonRootNode(jsonObject);

        assertThat(parent.iterator()).containsExactlyInAnyOrder(
                new GsonByNameNode(jsonObject, "one", parent),
                new GsonByNameNode(jsonObject, "two", parent),
                new GsonByNameNode(jsonObject, "three", parent)
        );
    }

}