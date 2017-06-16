package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;
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
public class DomNodeTest {

    @Mock private org.w3c.dom.Node node;

    private Node<org.w3c.dom.Node> nodeView;

    @Before
    public void setUp() {
        when(node.getTextContent()).thenReturn("text");

        nodeView = new DomNode(node);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareNode() {
        when(node.getNodeName()).thenReturn("node");
        QName result = nodeView.getNodeName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareNode() {
        when(node.getNamespaceURI()).thenReturn("http://www.example.com/my");
        when(node.getLocalName()).thenReturn("node");
        when(node.getPrefix()).thenReturn("my");
        QName result = nodeView.getNodeName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(nodeView.getText()).isEqualTo("text");
    }

}