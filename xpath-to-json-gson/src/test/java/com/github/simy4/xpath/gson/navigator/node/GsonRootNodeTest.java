package com.github.simy4.xpath.gson.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GsonRootNodeTest {

    private final JsonObject jsonObject = new JsonObject();
    private final GsonNode rootNode = new GsonRootNode(jsonObject);

    @Test
    void shouldReturnRootName() {
        assertThat(rootNode.getName()).isEqualTo(new QName(GsonNode.DOCUMENT));
    }

    @Test
    void shouldReturnRootNode() {
        assertThat(rootNode.get()).isSameAs(jsonObject);
    }

    @Test
    void shouldReplaceRootNodeOnSet() {
        JsonArray array = new JsonArray();
        rootNode.set(array);
        assertThat(rootNode.get()).isSameAs(array);
    }

    @Test
    void shouldThrowOnRemove() {
        assertThatThrownBy(rootNode::remove).isInstanceOf(XmlBuilderException.class);
    }

}