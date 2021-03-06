package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XomDocumentTest {

    private final Element root = new Element("root");

    private XomNode node;

    @BeforeEach
    void setUp() {
        root.appendChild("text");
        var document = new Document(root);
        node = new XomDocument(document);
    }

    @Test
    void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    void shouldReturnSingleRootNodeWhenObtainElements() {
        assertThat(node.elements()).asList().containsExactly(new XomElement(root));
    }

    @Test
    void shouldThrowExceptionWhenCreateAttribute() {
        assertThatThrownBy(() -> node.appendAttribute(new Attribute("attr", "")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldThrowBecauseRootElementShouldAlwaysBePresent() {
        assertThatThrownBy(() -> node.appendElement(new Element("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldReturnDocumentName() {
        assertThat(node.getName()).isEqualTo(new QName(XomNode.DOCUMENT));
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}