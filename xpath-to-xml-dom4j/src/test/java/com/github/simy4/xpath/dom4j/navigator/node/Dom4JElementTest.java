package com.github.simy4.xpath.dom4j.navigator.node;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Dom4JElementTest {

    @Mock private Element element;
    @Mock private Element child1;
    @Mock private Element child2;
    @Mock private Attribute attr1;
    @Mock private Attribute attr2;

    private Dom4jNode<Element> node;

    @Before
    public void setUp() {
        when(element.getText()).thenReturn("text");
        when(element.elementIterator()).thenReturn(asList(child1, child2).iterator());
        when(element.attributeIterator()).thenReturn(asList(attr1, attr2).iterator());
        when(element.addElement(any(org.dom4j.QName.class))).thenReturn(child1);

        node = new Dom4jElement(element);
    }

    @Test
    public void shouldReturnListOfAttributesWhenObtainAttributes() {
        assertThat(node.attributes()).contains(new Dom4jAttribute(attr1), new Dom4jAttribute(attr2));
    }

    @Test
    public void shouldReturnListOfElementsWhenObtainElements() {
        assertThat(node.elements()).contains(new Dom4jElement(child1), new Dom4jElement(child2));
    }

    @Test
    public void shouldAppendNewAttributeWhenCreateAttribute() {
        assertThat(node.createAttribute(new org.dom4j.QName("attr"))).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenCreateElement() {
        assertThat(node.createElement(new org.dom4j.QName("elem"))).isEqualTo(new Dom4jElement(child1));
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareElement() {
        when(element.getName()).thenReturn("node");
        when(element.getNamespace()).thenReturn(Namespace.NO_NAMESPACE);

        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareElement() {
        when(element.getName()).thenReturn("node");
        when(element.getNamespace()).thenReturn(new Namespace("my", "http://www.example.com/my"));

        QName result = node.getName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}