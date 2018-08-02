package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonByIndexNodeTest {

    private final JsonArray jsonArray = new JsonArray();
    private final GsonNode byIndexNode = new GsonByIndexNode(jsonArray, 1, null);

    @Before
    public void setUp() {
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
    }

    @Test
    public void shouldRetrieveElementByIndexOnGet() {
        assertThat(byIndexNode.get()).isEqualTo(new JsonPrimitive(2));
    }

    @Test
    public void shouldSetElementByIndexOnSet() {
        byIndexNode.set(new JsonPrimitive(4));

        assertThat(jsonArray).containsExactly(new JsonPrimitive(1), new JsonPrimitive(4), new JsonPrimitive(3));
    }

    @Test
    public void shouldRemoveElementByIndexOnRemove() {
        byIndexNode.remove();

        assertThat(jsonArray).containsExactly(new JsonPrimitive(1), new JsonPrimitive(3));
    }

    @Test
    public void shouldTraverseArray() {
        GsonNode parent = new GsonRootNode(jsonArray);

        assertThat(parent.iterator()).containsExactlyInAnyOrder(
                new GsonByIndexNode(jsonArray, 0, parent),
                new GsonByIndexNode(jsonArray, 1, parent),
                new GsonByIndexNode(jsonArray, 2, parent)
        );
    }

}