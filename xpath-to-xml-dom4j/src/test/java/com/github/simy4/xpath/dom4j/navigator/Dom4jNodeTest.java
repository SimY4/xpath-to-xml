package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.navigator.Node;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Dom4jNodeTest {

    @Mock private Element element;
    @Mock private Attribute attribute;

    private Node<org.dom4j.Node> nodeView;

    @Before
    public void setUp() {
        when(element.getNodeType()).thenReturn(org.dom4j.Node.ELEMENT_NODE);
        when(attribute.getNodeType()).thenReturn(org.dom4j.Node.ATTRIBUTE_NODE);
        when(element.getText()).thenReturn("text");

        nodeView = new Dom4jNode(element);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareElement() {
        when(element.getName()).thenReturn("node");
        when(element.getNamespace()).thenReturn(Namespace.NO_NAMESPACE);

        QName result = nodeView.getNodeName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareElement() {
        when(element.getName()).thenReturn("node");
        when(element.getNamespace()).thenReturn(new Namespace("my", "http://www.example.com/my"));

        QName result = nodeView.getNodeName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareAttribute() {
        nodeView = new Dom4jNode(attribute);
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(Namespace.NO_NAMESPACE);

        QName result = nodeView.getNodeName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareAttribute() {
        nodeView = new Dom4jNode(attribute);
        when(attribute.getName()).thenReturn("node");
        when(attribute.getNamespace()).thenReturn(new Namespace("my", "http://www.example.com/my"));

        QName result = nodeView.getNodeName();

        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(nodeView.getText()).isEqualTo("text");
    }

}