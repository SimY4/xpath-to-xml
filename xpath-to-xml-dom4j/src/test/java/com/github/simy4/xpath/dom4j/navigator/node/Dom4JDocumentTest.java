package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Node;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Dom4JDocumentTest {

  private Element root = DocumentHelper.createElement(new org.dom4j.QName("elem"));
  private Document document = DocumentHelper.createDocument(root);

  private Dom4jNode node;

  @BeforeEach
  void setUp() {
    node = new Dom4jDocument(document);
  }

  @Test
  void shouldReturnEmptyListWhenObtainAttributes() {
    assertThat(node.attributes()).isEmpty();
  }

  @Test
  void shouldReturnEmptyListWhenObtainElementsFromEmptyDocument() {
    document = DocumentHelper.createDocument();
    node = new Dom4jDocument(document);

    assertThat(node.elements()).isEmpty();
  }

  @Test
  void shouldReturnSingleRootNodeWhenObtainElements() {
    assertThat(node.elements()).asList().containsExactly(new Dom4jElement(root));
  }

  @Test
  void shouldThrowExceptionWhenCreateAttribute() {
    assertThatThrownBy(() -> node.createAttribute(new org.dom4j.QName("attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldSetRootNodeWhenCreateElement() {
    document = DocumentHelper.createDocument();
    node = new Dom4jDocument(document);

    Dom4jNode newRoot = node.createElement(new org.dom4j.QName("elem"));

    assertThat(newRoot).extracting("name").isEqualTo(new QName("elem"));
  }

  @Test
  void shouldThrowExceptionWhenRootElementAlreadyExist() {
    assertThatThrownBy(() -> node.createElement(new org.dom4j.QName("elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void shouldReturnDocumentName() {
    assertThat(node.getName()).isEqualTo(new QName(Dom4jNode.DOCUMENT));
  }

  @Test
  void shouldReturnNodeTextContent() {
    assertThat(node.getText()).isEmpty();
  }

  @Test
  void shouldSerializeAndDeserialize() throws IOException, ClassNotFoundException {
    // when
    Node deserializedNode = SerializationHelper.serializeAndDeserializeBack(node);

    // then
    assertThat(deserializedNode).extracting("name").isEqualTo(node.getName());
  }
}
