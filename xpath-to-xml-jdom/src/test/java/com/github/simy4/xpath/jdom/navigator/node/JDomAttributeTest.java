package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Node;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JDomAttributeTest {

    private JDomNode node;

    @BeforeEach
    void setUp() {
        Attribute attribute = new Attribute("attr", "text");
        attribute.setNamespace(Namespace.getNamespace("my", "http://www.example.com/my"));
        node = new JDomAttribute(attribute);
    }

    @Test
    void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenObtainElements() {
        assertThat(node.elements()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenAppendAttribute() {
        assertThatThrownBy(() -> node.appendAttribute(new Attribute("attr", "")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldThrowExceptionWhenAppendElement() {
        assertThatThrownBy(() -> node.appendElement(new Element("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldReturnNodeNameWithNamespaceUri() {
        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "attr", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

    @Test
    void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        // when
        Node deserializedNode = SerializationHelper.serializeAndDeserializeBack(node);

        // then
        assertThat(deserializedNode).extracting("node").containsExactly(node);
    }

}