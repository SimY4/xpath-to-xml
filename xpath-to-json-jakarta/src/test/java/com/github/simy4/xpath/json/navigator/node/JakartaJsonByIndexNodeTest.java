package com.github.simy4.xpath.json.navigator.node;

import jakarta.json.JsonArray;
import jakarta.json.spi.JsonProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JakartaJsonByIndexNodeTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonArray jsonArray = jsonProvider.createArrayBuilder()
            .add(1)
            .add(2)
            .add(3)
            .build();
    private final JakartaJsonNode rootNode = new JakartaJsonRootNode(jsonArray);
    private final JakartaJsonNode byIndexNode = new JakartaJsonByIndexNode(1, rootNode);

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
    void shouldRemoveElementByIndexOnSetNull() {
        byIndexNode.set(jsonProvider, null);

        assertThat(rootNode.get().asJsonArray())
                .containsExactly(jsonProvider.createValue(1), jsonProvider.createValue(3));
    }

    @Test
    void shouldTraverseArrayAttributes() {
        var parent = new JakartaJsonRootNode(jsonArray);

        assertThat(parent.attributes()).containsExactlyInAnyOrder(
                new JakartaJsonByIndexNode(0, parent),
                new JakartaJsonByIndexNode(1, parent),
                new JakartaJsonByIndexNode(2, parent)
        );
    }

    @Test
    void shouldTraverseArrayElements() {
        var parent = new JakartaJsonRootNode(jsonArray);

        assertThat(parent.elements()).isEmpty();
    }

}