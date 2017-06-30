package com.github.simy4.xpath.xom.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import nu.xom.Attribute;
import nu.xom.Element;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;

public class XomAttributeTest {

    private XomNode<Attribute> node;

    @Before
    public void setUp() {
        Attribute attribute = new Attribute("attr", "text");
        attribute.setNamespace("my", "http://www.example.com/my");
        node = new XomAttribute(attribute);
    }

    @Test
    public void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListWhenObtainElements() {
        assertThat(node.elements()).isEmpty();
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenAppendAttribute() {
        node.appendAttribute(new Attribute("attr", ""));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenAppendElement() {
        node.appendElement(new Element("elem"));
    }

    @Test
    public void shouldReturnNodeNameWithNamespaceUri() {
        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "attr", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}