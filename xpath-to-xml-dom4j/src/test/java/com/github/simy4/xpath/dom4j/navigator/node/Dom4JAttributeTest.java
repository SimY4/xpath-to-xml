package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.XMLConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Dom4JAttributeTest {

    @Mock private Attribute attribute;

    private Dom4jNode node;

    @BeforeEach
    void setUp() {
        when(attribute.getText()).thenReturn("text");

        node = new Dom4jAttribute(attribute);
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
    void shouldThrowExceptionWhenCreateAttribute() {
        assertThatThrownBy(() -> node.createAttribute(new org.dom4j.QName("attr")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldThrowExceptionWhenCreateElement() {
        assertThatThrownBy(() -> node.createElement(new org.dom4j.QName("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldReturnNodeNameForNamespaceUnawareAttribute() {
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(Namespace.NO_NAMESPACE);

        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    void shouldReturnNodeNameForNamespaceAwareAttribute() {
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(new Namespace("my", "http://www.example.com/my"));

        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}