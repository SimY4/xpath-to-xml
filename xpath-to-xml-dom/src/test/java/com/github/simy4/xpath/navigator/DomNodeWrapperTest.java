package com.github.simy4.xpath.navigator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomNodeWrapperTest {

    @Mock private Node node;

    private NodeWrapper<Node> nodeWrapper;

    @Before
    public void setUp() {
        when(node.getTextContent()).thenReturn("text");

        nodeWrapper = new DomNodeWrapper(node);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceUnawareNode() {
        when(node.getNodeName()).thenReturn("node");
        QName result = nodeWrapper.getNodeName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    public void shouldReturnNodeNameForNamespaceAwareNode() {
        when(node.getNamespaceURI()).thenReturn("http://www.example.com/my");
        when(node.getLocalName()).thenReturn("node");
        when(node.getPrefix()).thenReturn("my");
        QName result = nodeWrapper.getNodeName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(nodeWrapper.getText()).isEqualTo("text");
    }

}