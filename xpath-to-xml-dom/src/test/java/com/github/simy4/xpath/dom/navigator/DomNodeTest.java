package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.navigator.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomNodeTest {

    @Mock private org.w3c.dom.Node node;

    private Node nodeView;

    @BeforeEach
    void setUp() {
        when(node.getTextContent()).thenReturn("text");

        nodeView = new DomNode(node);
    }

    @Test
    void shouldReturnNodeNameForNamespaceUnawareNode() {
        when(node.getNodeName()).thenReturn("node");
        QName result = nodeView.getName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly(XMLConstants.NULL_NS_URI, "node", XMLConstants.DEFAULT_NS_PREFIX);
    }

    @Test
    void shouldReturnNodeNameForNamespaceAwareNode() {
        when(node.getNamespaceURI()).thenReturn("http://www.example.com/my");
        when(node.getLocalName()).thenReturn("node");
        when(node.getPrefix()).thenReturn("my");
        QName result = nodeView.getName();
        assertThat(result).extracting("namespaceURI", "localPart", "prefix")
                .containsExactly("http://www.example.com/my", "node", "my");
    }

    @Test
    void shouldReturnNodeTextContent() {
        assertThat(nodeView.getText()).isEqualTo("text");
    }

    @Test
    void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException, ParserConfigurationException {
        // given
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        nodeView = new DomNode(document);

        // when
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(nodeView);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Node deserialized = (Node) new ObjectInputStream(in).readObject();

        // then
        assertThat(deserialized).hasToString(document.toString());
    }

}