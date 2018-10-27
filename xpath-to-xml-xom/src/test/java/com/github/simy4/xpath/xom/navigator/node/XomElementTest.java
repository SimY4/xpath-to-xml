package com.github.simy4.xpath.xom.navigator.node;

import nu.xom.Attribute;
import nu.xom.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XomElementTest {

    private XomNode node;
    private final Attribute attr1 = new Attribute("attr1", "text");
    private final Attribute attr2 = new Attribute("attr2", "text");
    private final Attribute attr3 = new Attribute("attr3", "text");
    private final Element child1 = new Element("child1");
    private final Element child2 = new Element("child2");
    private final Element child3 = new Element("child3");

    @BeforeEach
    void setUp() {
        var element = new Element("elem", "http://www.example.com/my");
        element.setNamespacePrefix("my");
        element.addAttribute(attr1);
        element.addAttribute(attr2);
        element.addAttribute(attr3);
        element.appendChild("text");
        element.appendChild(child1);
        element.appendChild(child2);
        element.appendChild(child3);

        node = new XomElement(element);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnListOfAttributesWhenObtainAttributes() {
        assertThat((Iterable<XomNode>) node.attributes())
                .containsExactly(new XomAttribute(attr1), new XomAttribute(attr2), new XomAttribute(attr3));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnListOfElementsWhenObtainElements() {
        assertThat((Iterable<XomNode>) node.elements())
                .containsExactly(new XomElement(child1), new XomElement(child2), new XomElement(child3));
    }

    @Test
    void shouldAppendNewAttributeWhenAppendAttribute() {
        assertThat(node.appendAttribute(new Attribute("attr", ""))).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenAppendElement() {
        var elem = new Element("elem");
        assertThat(node.appendElement(elem)).isEqualTo(new XomElement(elem));
    }

    @Test
    void shouldReturnNodeNameWithNamespaceUri() {
        var result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "elem", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}