package com.github.simy4.xpath.jdom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JDomDocumentTest {

    private final Element root = new Element("root");

    private JDomNode node;

    @BeforeEach
    void setUp() {
        root.addContent("text");
        var document = new Document(root);
        node = new JDomDocument(document);
    }

    @Test
    void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    void shouldReturnSingleRootNodeWhenObtainElements() {
        assertThat(node.elements()).asList().containsExactly(new JDomElement(root));
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
        assertThat(node.getName()).isEqualTo(new QName(JDomNode.DOCUMENT));
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}