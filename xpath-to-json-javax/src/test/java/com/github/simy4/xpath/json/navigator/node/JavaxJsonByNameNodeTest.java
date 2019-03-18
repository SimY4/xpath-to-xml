package com.github.simy4.xpath.json.navigator.node;

import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class JavaxJsonByNameNodeTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonObject jsonObject = jsonProvider.createObjectBuilder()
            .add("one", 1)
            .add("two", 2)
            .add("three", 3)
            .build();
    private final JavaxJsonNode rootNode = new JavaxJsonRootNode(jsonObject);
    private final JavaxJsonNode byNameNode = new JavaxJsonByNameNode("two", rootNode);

    @Test
    void shouldRetrieveElementByIndexOnGet() {
        assertThat(byNameNode.get()).isEqualTo(jsonProvider.createValue(2));
    }

    @Test
    void shouldSetElementByIndexOnSet() {
        byNameNode.set(jsonProvider, jsonProvider.createValue(4));

        assertThat(rootNode.get().asJsonObject()).containsExactly(
                entry("one", jsonProvider.createValue(1)),
                entry("two", jsonProvider.createValue(4)),
                entry("three", jsonProvider.createValue(3))
        );
    }

    @Test
    void shouldRemoveElementByIndexOnRemove() {
        byNameNode.remove(jsonProvider);

        assertThat(rootNode.get().asJsonObject()).containsExactly(
                entry("one", jsonProvider.createValue(1)),
                entry("three", jsonProvider.createValue(3))
        );
    }

    @Test
    void shouldTraverseObject() {
        var parent = new JavaxJsonRootNode(jsonObject);

        assertThat(parent).containsExactlyInAnyOrder(
                new JavaxJsonByNameNode("one", parent),
                new JavaxJsonByNameNode("two", parent),
                new JavaxJsonByNameNode("three", parent)
        );
    }

}