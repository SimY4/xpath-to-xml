package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.XMLConstants;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DomNodeTest {

    @Mock(serializable = true) private org.w3c.dom.Node node;

    private Node nodeView;

    @BeforeEach
    void setUp() {
        nodeView = new DomNode(node);
    }

    @Test
    void shouldReturnNodeNameForNamespaceUnawareNode() {
        when(node.getNodeName()).thenReturn("node");
        var result = nodeView.getName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    void shouldReturnNodeNameForNamespaceAwareNode() {
        when(node.getNamespaceURI()).thenReturn("http://www.example.com/my");
        when(node.getLocalName()).thenReturn("node");
        when(node.getPrefix()).thenReturn("my");
        var result = nodeView.getName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        when(node.getTextContent()).thenReturn("text");

        assertThat(nodeView.getText()).isEqualTo("text");
    }

    @Test
    void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        // given
        when(node.getNodeName()).thenReturn("node");

        // when
        var deserializedNode = SerializationHelper.serializeAndDeserializeBack(nodeView);

        // then
        assertThat(deserializedNode.getName()).isEqualTo(nodeView.getName());
    }

}