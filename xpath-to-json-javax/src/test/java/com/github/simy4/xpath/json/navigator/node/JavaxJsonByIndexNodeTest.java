package com.github.simy4.xpath.json.navigator.node;

import org.junit.jupiter.api.Test;

import javax.json.JsonArray;
import javax.json.spi.JsonProvider;

import static org.assertj.core.api.Assertions.assertThat;

class JavaxJsonByIndexNodeTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonArray jsonArray = jsonProvider.createArrayBuilder()
            .add(1)
            .add(2)
            .add(3)
            .build();
    private final JavaxJsonNode rootNode = new JavaxJsonRootNode(jsonArray);
    private final JavaxJsonNode byIndexNode = new JavaxJsonByIndexNode(jsonArray, 1, rootNode);

    @Test
    void shouldRetrieveElementByIndexOnGet() {
        assertThat(byIndexNode.get()).isEqualTo(jsonProvider.createValue(2));
    }

    @Test
    void shouldSetElementByIndexOnSet() {
        byIndexNode.set(jsonProvider, jsonProvider.createValue(4));

        assertThat(rootNode.get().asJsonArray()).containsExactly(
                jsonProvider.createValue(1), jsonProvider.createValue(4), jsonProvider.createValue(3));
    }

    @Test
    void shouldRemoveElementByIndexOnRemove() {
        byIndexNode.remove(jsonProvider);

        assertThat(rootNode.get().asJsonArray())
                .containsExactly(jsonProvider.createValue(1), jsonProvider.createValue(3));
    }

    @Test
    void shouldTraverseArray() {
        JavaxJsonNode parent = new JavaxJsonRootNode(jsonArray);

        assertThat(parent.iterator()).containsExactlyInAnyOrder(
                new JavaxJsonByIndexNode(jsonArray, 0, parent),
                new JavaxJsonByIndexNode(jsonArray, 1, parent),
                new JavaxJsonByIndexNode(jsonArray, 2, parent)
        );
    }

}