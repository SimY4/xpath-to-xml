package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Attribute;
import org.dom4j.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Dom4JAttributeTest {

    @Mock private Attribute attribute;

    private Dom4jNode<Attribute> node;

    @Before
    public void setUp() {
        when(attribute.getText()).thenReturn("text");

        node = new Dom4jAttribute(attribute);
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
    public void shouldThrowExceptionWhenCreateAttribute() {
        node.createAttribute(new org.dom4j.QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenCreateElement() {
        node.createElement(new org.dom4j.QName("elem"));
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareElement() {
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(Namespace.NO_NAMESPACE);

        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareElement() {
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(new Namespace("my", "http://www.example.com/my"));

        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}