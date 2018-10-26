package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Dom4JDocumentTest {

    @Mock private Document document;
    @Mock private Element root;

    private Dom4jNode node;

    @BeforeEach
    void setUp() {
        when(document.getText()).thenReturn("text");
        when(document.addElement(any(org.dom4j.QName.class))).thenReturn(root);

        node = new Dom4jDocument(document);
    }

    @Test
    void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenObtainElementsFromEmptyDocument() {
        assertThat(node.elements()).isEmpty();
    }

    @Test
    void shouldReturnSingleRootNodeWhenObtainElements() {
        when(document.getRootElement()).thenReturn(root);
        assertThat(node.elements()).asList().containsExactly(new Dom4jElement(root));
    }

    @Test
    void shouldThrowExceptionWhenCreateAttribute() {
        assertThatThrownBy(() -> node.createAttribute(new org.dom4j.QName("attr")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldSetRootNodeWhenCreateElement() {
        assertThat(node.createElement(new org.dom4j.QName("elem"))).isEqualTo(new Dom4jElement(root));
    }

    @Test
    void shouldThrowExceptionWhenRootElementAlreadyExist() {
        when(document.getRootElement()).thenReturn(root);
        assertThatThrownBy(() -> node.createElement(new org.dom4j.QName("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldReturnDocumentName() {
        assertThat(node.getName()).isEqualTo(new QName(Dom4jNode.DOCUMENT));
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}