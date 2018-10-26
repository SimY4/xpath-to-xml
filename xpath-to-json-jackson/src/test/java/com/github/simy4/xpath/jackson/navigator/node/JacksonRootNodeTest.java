package com.github.simy4.xpath.jackson.navigator.node;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.XmlBuilderException;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JacksonRootNodeTest {

    private final ObjectNode jsonObject = new ObjectNode(JsonNodeFactory.instance);
    private final JacksonNode rootNode = new JacksonRootNode(jsonObject);

    @Test
    void shouldReturnRootName() {
        assertThat(rootNode.getName()).isEqualTo(new QName(JacksonNode.DOCUMENT));
    }

    @Test
    void shouldReturnRootNode() {
        assertThat(rootNode.get()).isSameAs(jsonObject);
    }

    @Test
    void shouldReplaceRootNodeOnSet() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        rootNode.set(array);
        assertThat(rootNode.get()).isSameAs(array);
    }

    @Test
    void shouldThrowOnRemove() {
        assertThatThrownBy(rootNode::remove).isInstanceOf(XmlBuilderException.class);
    }

}