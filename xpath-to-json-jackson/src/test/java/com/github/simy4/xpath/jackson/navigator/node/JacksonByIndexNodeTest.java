package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonByIndexNodeTest {

    private final ArrayNode jsonArray = new ArrayNode(JsonNodeFactory.instance);
    private final JacksonNode byIndexNode = new JacksonByIndexNode(jsonArray, 1, null);

    @BeforeEach
    void setUp() {
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
    }

    @Test
    void shouldRetrieveElementByIndexOnGet() {
        assertThat(byIndexNode.get()).isEqualTo(new IntNode(2));
    }

    @Test
    void shouldSetElementByIndexOnSet() {
        byIndexNode.set(new IntNode(4));

        assertThat(jsonArray).containsExactly(new IntNode(1), new IntNode(4), new IntNode(3));
    }

    @Test
    void shouldRemoveElementByIndexOnRemove() {
        byIndexNode.remove();

        assertThat(jsonArray).containsExactly(new IntNode(1), new IntNode(3));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldTraverseArrayAttributes() {
        JacksonNode parent = new JacksonRootNode(jsonArray);

        assertThat((Iterable<JacksonNode>) parent.attributes()).containsExactlyInAnyOrder(
                new JacksonByIndexNode(jsonArray, 0, parent),
                new JacksonByIndexNode(jsonArray, 1, parent),
                new JacksonByIndexNode(jsonArray, 2, parent)
        );
    }

    @Test
    void shouldTraverseArrayElements() {
        JacksonNode parent = new JacksonRootNode(jsonArray);

        assertThat((Iterable<?>) parent.elements()).isEmpty();
    }

}