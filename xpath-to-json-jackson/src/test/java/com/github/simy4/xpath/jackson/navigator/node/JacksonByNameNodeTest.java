package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonByNameNodeTest {

    private final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
    private final JacksonNode byNameNode = new JacksonByNameNode(jsonObject, "two", null);

    @Before
    public void setUp() {
        jsonObject.set("one", new IntNode(1));
        jsonObject.set("two", new IntNode(2));
        jsonObject.set("three", new IntNode(3));
    }

    @Test
    public void shouldRetrieveElementByIndexOnGet() {
        assertThat(byNameNode.get()).isEqualTo(new IntNode(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSetElementByIndexOnSet() {
        byNameNode.set(new IntNode(4));

        assertThat(jsonObject.fields()).containsExactly(
                new AbstractMap.SimpleEntry<String, JsonNode>("one", new IntNode(1)),
                new AbstractMap.SimpleEntry<String, JsonNode>("two", new IntNode(4)),
                new AbstractMap.SimpleEntry<String, JsonNode>("three", new IntNode(3))
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRemoveElementByIndexOnRemove() {
        byNameNode.remove();

        assertThat(jsonObject.fields()).containsExactly(
                new AbstractMap.SimpleEntry<String, JsonNode>("one", new IntNode(1)),
                new AbstractMap.SimpleEntry<String, JsonNode>("three", new IntNode(3))
        );
    }

}