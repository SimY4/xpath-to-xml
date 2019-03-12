package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class JacksonByNameNodeTest {

    private final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
    private final JacksonNode byNameNode = new JacksonByNameNode(jsonObject, "two", null);

    @BeforeEach
    void setUp() {
        jsonObject.set("one", new IntNode(1));
        jsonObject.set("two", new IntNode(2));
        jsonObject.set("three", new IntNode(3));
    }

    @Test
    void shouldRetrieveElementByIndexOnGet() {
        assertThat(byNameNode.get()).isEqualTo(new IntNode(2));
    }

    @Test
    void shouldSetElementByIndexOnSet() {
        byNameNode.set(new IntNode(4));

        assertThat(jsonObject.fields()).toIterable().containsExactly(
                entry("one", new IntNode(1)),
                entry("two", new IntNode(4)),
                entry("three", new IntNode(3))
        );
    }

    @Test
    void shouldRemoveElementByIndexOnRemove() {
        byNameNode.remove();

        assertThat(jsonObject.fields()).toIterable().containsExactly(
                entry("one", new IntNode(1)),
                entry("three", new IntNode(3))
        );
    }

    @Test
    void shouldTraverseObject() {
        JacksonNode parent = new JacksonRootNode(jsonObject);

        assertThat(parent).containsExactlyInAnyOrder(
                new JacksonByNameNode(jsonObject, "one", parent),
                new JacksonByNameNode(jsonObject, "two", parent),
                new JacksonByNameNode(jsonObject, "three", parent)
        );
    }

}