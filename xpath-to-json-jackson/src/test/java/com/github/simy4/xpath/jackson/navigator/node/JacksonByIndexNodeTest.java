package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonByIndexNodeTest {

    private final ArrayNode jsonArray = new ArrayNode(JsonNodeFactory.instance);
    private final JacksonNode byIndexNode = new JacksonByIndexNode(jsonArray, 1, null);

    @Before
    public void setUp() {
        jsonArray.add(1);
        jsonArray.add(2);
        jsonArray.add(3);
    }

    @Test
    public void shouldRetrieveElementByIndexOnGet() {
        assertThat(byIndexNode.get()).isEqualTo(new IntNode(2));
    }

    @Test
    public void shouldSetElementByIndexOnSet() {
        byIndexNode.set(new IntNode(4));

        assertThat(jsonArray).containsExactly(new IntNode(1), new IntNode(4), new IntNode(3));
    }

    @Test
    public void shouldRemoveElementByIndexOnRemove() {
        byIndexNode.remove();

        assertThat(jsonArray).containsExactly(new IntNode(1), new IntNode(3));
    }

    @Test
    public void shouldTraverseArray() {
        JacksonNode parent = new JacksonRootNode(jsonArray);

        assertThat(parent.iterator()).containsExactlyInAnyOrder(
                new JacksonByIndexNode(jsonArray, 0, parent),
                new JacksonByIndexNode(jsonArray, 1, parent),
                new JacksonByIndexNode(jsonArray, 2, parent)
        );
    }

}