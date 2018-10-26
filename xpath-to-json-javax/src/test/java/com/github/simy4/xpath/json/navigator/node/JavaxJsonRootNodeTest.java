package com.github.simy4.xpath.json.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.junit.jupiter.api.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaxJsonRootNodeTest {

    private final JsonObject jsonObject = JsonValue.EMPTY_JSON_OBJECT;
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
        JsonArray array = JsonValue.EMPTY_JSON_ARRAY;
        rootNode.set(array);
        assertThat(rootNode.get()).isSameAs(array);
    }

    @Test
    void shouldThrowOnRemove() {
        assertThatThrownBy(rootNode::remove).isInstanceOf(XmlBuilderException.class);
    }

}