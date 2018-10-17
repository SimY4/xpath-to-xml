package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.junit.jupiter.api.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaxJsonRootNodeTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private final JsonObject jsonObject = jsonProvider.createObjectBuilder().build();
    private final JavaxJsonNode rootNode = new JavaxJsonRootNode(jsonObject);

    @Test
    void shouldReturnRootName() {
        assertThat(rootNode.getName()).isEqualTo(new QName("_root_"));
    }

    @Test
    void shouldReturnRootNode() {
        assertThat(rootNode.get()).isSameAs(jsonObject);
    }

    @Test
    void shouldReplaceRootNodeOnSet() {
        JsonArray array = jsonProvider.createArrayBuilder().build();
        rootNode.set(array);
        assertThat(rootNode.get()).isSameAs(array);
    }

    @Test
    void shouldThrowOnRemove() {
        assertThatThrownBy(rootNode::remove).isInstanceOf(XmlBuilderException.class);
    }

}