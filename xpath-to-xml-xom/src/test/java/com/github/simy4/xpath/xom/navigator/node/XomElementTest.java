package com.github.simy4.xpath.xom.navigator.node;

import nu.xom.Attribute;
import nu.xom.Element;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;

public class XomElementTest {

    private XomNode<Element> node;
    private final Attribute attr1 = new Attribute("attr1", "text");
    private final Attribute attr2 = new Attribute("attr2", "text");
    private final Attribute attr3 = new Attribute("attr3", "text");
    private final Element child1 = new Element("child1");
    private final Element child2 = new Element("child2");
    private final Element child3 = new Element("child3");

    @Before
    public void setUp() {
        Element element = new Element("elem", "http://www.example.com/my");
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
    public void shouldReturnListOfAttributesWhenObtainAttributes() {
        assertThat(node.attributes()).contains(new XomAttribute(attr1), new XomAttribute(attr2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnListOfElementsWhenObtainElements() {
        assertThat(node.elements()).contains(new XomElement(child1), new XomElement(child2));
    }

    @Test
    public void shouldAppendNewAttributeWhenAppendAttribute() {
        assertThat(node.appendAttribute(new Attribute("attr", ""))).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenAppendElement() {
        Element elem = new Element("elem");
        assertThat(node.appendElement(elem)).isEqualTo(new XomElement(elem));
    }

    @Test
    public void shouldReturnNodeNameWithNamespaceUri() {
        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "elem", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}