package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.helpers.SerializationHelper;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class Dom4JElementTest {

    private Element element = DocumentHelper.createElement(new org.dom4j.QName("node"));
    private Element child1 = DocumentHelper.createElement(new org.dom4j.QName("child1"));
    private Element child2 = DocumentHelper.createElement(new org.dom4j.QName("child2"));
    private Attribute attr1 = DocumentHelper.createAttribute(element, new org.dom4j.QName("attr1"), "");
    private Attribute attr2 = DocumentHelper.createAttribute(element, new org.dom4j.QName("attr2"), "");

    private Dom4jNode node;

    @BeforeEach
    void setUp() {
        element.add(child1);
        element.add(child2);
        element.add(attr1);
        element.add(attr2);
        element.setText("text");

        node = new Dom4jElement(element);
    }

    @Test
    void shouldReturnListOfAttributesWhenObtainAttributes() {
        assertThat(node.attributes())
                .containsExactly(new Dom4jAttribute(attr1), new Dom4jAttribute(attr2));
    }

    @Test
    void shouldReturnListOfElementsWhenObtainElements() {
        assertThat(node.elements())
                .containsExactly(new Dom4jElement(child1), new Dom4jElement(child2));
    }

    @Test
    void shouldAppendNewAttributeWhenCreateAttribute() {
        assertThat(node.createAttribute(new org.dom4j.QName("attr"))).isNotNull();
    }

    @Test
    void shouldAppendNewElementWhenCreateElement() {
        var elem = node.createElement(new org.dom4j.QName("elem"));
        assertThat(elem).extracting("name").isEqualTo(new QName("elem"));
    }

    @Test
    void shouldReturnNodeNameForNamespaceUnawareElement() {
        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    void shouldReturnNodeNameForNamespaceAwareElement() {
        element = DocumentHelper.createElement(new org.dom4j.QName("node",
                new Namespace("my", "http://www.example.com/my")));
        node = new Dom4jElement(element);

        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

    @Test
    void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        // when
        var deserializedNode = SerializationHelper.serializeAndDeserializeBack(node);

        // then
        assertThat(deserializedNode).extracting("name").isEqualTo(node.getName());
    }

}