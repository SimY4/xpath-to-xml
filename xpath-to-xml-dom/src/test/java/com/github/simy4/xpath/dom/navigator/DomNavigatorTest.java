package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DomNavigatorTest {

  @Mock private Document root;
  @Mock private Element xml;
  @Mock private Attr attr;
  @Mock private NamedNodeMap attributes;
  @Mock private Node child1;
  @Mock private Node child2;
  @Mock private Node child3;

  private Navigator<DomNode> navigator;

  @BeforeEach
  void setUp() {
    when(root.createAttribute(anyString())).thenReturn(attr);
    when(root.createAttributeNS(anyString(), anyString())).thenReturn(attr);
    when(root.createElement(anyString())).thenReturn(xml);
    when(root.createElementNS(anyString(), anyString())).thenReturn(xml);

    when(child1.getNodeType()).thenReturn(Node.ELEMENT_NODE);
    when(child2.getNodeType()).thenReturn(Node.ELEMENT_NODE);
    when(child3.getNodeType()).thenReturn(Node.ELEMENT_NODE);

    when(xml.getNodeType()).thenReturn(Node.ELEMENT_NODE);
    when(xml.getOwnerDocument()).thenReturn(root);
    when(xml.getParentNode()).thenReturn(root);
    when(xml.getFirstChild()).thenReturn(child1);
    when(xml.getAttributes()).thenReturn(attributes);
    when(xml.cloneNode(true)).thenReturn(xml);

    when(attributes.getLength()).thenReturn(3);
    when(attributes.item(0)).thenReturn(child1);
    when(attributes.item(1)).thenReturn(child2);
    when(attributes.item(2)).thenReturn(child3);

    when(child1.getNextSibling()).thenReturn(child2);
    when(child2.getNextSibling()).thenReturn(child3);

    navigator = new DomNavigator(xml);
  }

  @Test
  void testRootNode() {
    assertThat(navigator.root()).hasFieldOrPropertyWithValue("node", root);
  }

  @Test
  void testParentOfRegularNode() {
    assertThat(navigator.parentOf(new DomNode(xml))).hasFieldOrPropertyWithValue("node", root);
  }

  @Test
  void testParentOfRootNode() {
    assertThat(navigator.parentOf(new DomNode(root))).isNull();
  }

  @Test
  void testElementsOf() {
    assertThat(navigator.elementsOf(new DomNode(xml)))
        .extracting("node", Node.class)
        .containsExactly(child1, child2, child3);
  }

  @Test
  void testAttributesOf() {
    assertThat(navigator.attributesOf(new DomNode(xml)))
        .extracting("node", Node.class)
        .containsExactly(child1, child2, child3);
  }

  @Test
  void testCreateAttributeSuccess() {
    assertThat(navigator.createAttribute(new DomNode(xml), new QName("attr"))).isNotNull();
    verify(root).createAttribute("attr");
    verify(xml).setAttributeNode(attr);
  }

  @Test
  void testCreateNsAttributeSuccess() {
    assertThat(
            navigator.createAttribute(new DomNode(xml), new QName("http://example.com/my", "attr")))
        .isNotNull();
    verify(root).createAttributeNS("http://example.com/my", "attr");
    verify(xml).setAttributeNode(attr);
  }

  @Test
  void testCreateAttributeFailureWhenParentIsNotAnElement() {
    assertThatThrownBy(() -> navigator.createAttribute(new DomNode(root), new QName("attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testCreateAttributeFailure() {
    when(root.createAttribute(anyString())).thenThrow(DOMException.class);
    assertThatThrownBy(() -> navigator.createAttribute(new DomNode(xml), new QName("attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testCreateNsAttributeFailure() {
    when(root.createAttributeNS(anyString(), anyString())).thenThrow(DOMException.class);
    assertThatThrownBy(
            () ->
                navigator.createAttribute(
                    new DomNode(xml), new QName("http://example.com/my", "attr")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testCreateElementSuccess() {
    assertThat(navigator.createElement(new DomNode(xml), new QName("elem"))).isNotNull();
    verify(root).createElement("elem");
    verify(xml).appendChild(xml);
  }

  @Test
  void testCreateNsElementSuccess() {
    assertThat(
            navigator.createElement(new DomNode(xml), new QName("http://example.com/my", "elem")))
        .isNotNull();
    verify(root).createElementNS("http://example.com/my", "elem");
    verify(xml).appendChild(xml);
  }

  @Test
  void testCreateElementFailure() {
    when(root.createElement(anyString())).thenThrow(DOMException.class);
    assertThatThrownBy(() -> navigator.createElement(new DomNode(xml), new QName("elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testCreateNsElementFailure() {
    when(root.createElementNS(anyString(), anyString())).thenThrow(DOMException.class);
    assertThatThrownBy(
            () ->
                navigator.createElement(
                    new DomNode(xml), new QName("http://example.com/my", "elem")))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testPrependCopySuccess() {
    navigator.prependCopy(new DomNode(xml));
    verify(xml).cloneNode(true);
    verify(root).insertBefore(xml, xml);
  }

  @Test
  void testPrependCopyNoParent() {
    assertThatThrownBy(() -> navigator.prependCopy(new DomNode(root)))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testPrependCopyFailure() {
    doThrow(DOMException.class).when(root).insertBefore(any(Node.class), any(Node.class));
    assertThatThrownBy(() -> navigator.prependCopy(new DomNode(xml)))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testSetTextSuccess() {
    navigator.setText(new DomNode(xml), "text");
    verify(xml).setTextContent("text");
  }

  @Test
  void testSetTextFailure() {
    doThrow(DOMException.class).when(xml).setTextContent(anyString());
    assertThatThrownBy(() -> navigator.setText(new DomNode(xml), "text"))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testRemoveSuccess() {
    navigator.remove(new DomNode(xml));
    verify(root).removeChild(xml);
  }

  @Test
  void testRemoveNoParent() {
    assertThatThrownBy(() -> navigator.remove(new DomNode(root)))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testRemoveFailure() {
    when(root.removeChild(any(Node.class))).thenThrow(DOMException.class);
    assertThatThrownBy(() -> navigator.remove(new DomNode(xml)))
        .isInstanceOf(XmlBuilderException.class);
  }
}
