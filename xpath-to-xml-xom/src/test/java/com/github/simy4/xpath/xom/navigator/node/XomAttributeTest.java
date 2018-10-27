package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XomAttributeTest {

    private XomNode node;

    @BeforeEach
    void setUp() {
        var attribute = new Attribute("attr", "text");
        attribute.setNamespace("my", "http://www.example.com/my");
        node = new XomAttribute(attribute);
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
        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "attr", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}