package com.github.simy4.xpath.gson.navigator.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GsonByIndexNodeTest {

    private final JsonArray jsonArray = new JsonArray();
    private final GsonNode byIndexNode = new GsonByIndexNode(jsonArray, 1, null);

    @BeforeEach
    void setUp() {
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
    }

    @Test
    void shouldRetrieveElementByIndexOnGet() {
        assertThat(byIndexNode.get()).isEqualTo(new JsonPrimitive(2));
    }

    @Test
    void shouldSetElementByIndexOnSet() {
        byIndexNode.set(new JsonPrimitive(4));

        assertThat(jsonArray).containsExactly(new JsonPrimitive(1), new JsonPrimitive(4), new JsonPrimitive(3));
    }

    @Test
    void shouldRemoveElementByIndexOnRemove() {
        byIndexNode.remove();

        assertThat(jsonArray).containsExactly(new JsonPrimitive(1), new JsonPrimitive(3));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldTraverseArrayAttributes() {
        GsonNode parent = new GsonRootNode(jsonArray);

        assertThat((Iterable<GsonNode>) parent.attributes()).containsExactlyInAnyOrder(
                new GsonByIndexNode(jsonArray, 0, parent),
                new GsonByIndexNode(jsonArray, 1, parent),
                new GsonByIndexNode(jsonArray, 2, parent)
        );
    }

    @Test
    void shouldTraverseArrayElements() {
        GsonNode parent = new GsonRootNode(jsonArray);

        assertThat((Iterable<?>) parent.elements()).isEmpty();
    }

}