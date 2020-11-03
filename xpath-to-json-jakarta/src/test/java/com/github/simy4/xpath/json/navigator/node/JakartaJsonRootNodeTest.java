package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JakartaJsonRootNodeTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonObject jsonObject = JsonValue.EMPTY_JSON_OBJECT;
    private final JakartaJsonNode rootNode = new JakartaJsonRootNode(jsonObject);

    @Test
    void shouldReturnRootName() {
        assertThat(rootNode.getName()).isEqualTo(new QName(JakartaJsonNode.DOCUMENT));
    }

    @Test
    void shouldReturnRootNode() {
        assertThat(rootNode.get()).isSameAs(jsonObject);
    }

    @Test
    void shouldReplaceRootNodeOnSet() {
        var array = JsonValue.EMPTY_JSON_ARRAY;
        rootNode.set(jsonProvider, array);
        assertThat(rootNode.get()).isSameAs(array);
    }

    @Test
    void shouldThrowOnSetNull() {
        assertThatThrownBy(() -> rootNode.set(jsonProvider, null)).isInstanceOf(XmlBuilderException.class);
    }

}