package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XomDocumentTest {

    private final Element root = new Element("root");

    private XomNode<Document> node;

    @Before
    public void setUp() {
        root.appendChild("text");
        Document document = new Document(root);
        node = new XomDocument(document);
    }

    @Test
    public void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnSingleRootNodeWhenObtainElements() {
        assertThat(node.elements()).containsExactly(new XomElement(root));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenCreateAttribute() {
        node.appendAttribute(new Attribute("attr", ""));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowBecauseRootElementShouldAlwaysBePresent() {
        node.appendElement(new Element("elem"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnGetName() {
        node.getName();
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}