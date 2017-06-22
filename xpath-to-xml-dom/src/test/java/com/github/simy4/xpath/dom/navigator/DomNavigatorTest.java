package com.github.simy4.xpath.dom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomNavigatorTest {

    @Mock private Document root;
    @Mock private Element xml;
    @Mock private Attr attr;
    @Mock private NamedNodeMap attributes;
    @Mock private org.w3c.dom.Node child1;
    @Mock private org.w3c.dom.Node child2;
    @Mock private org.w3c.dom.Node child3;

    private Navigator<org.w3c.dom.Node> navigator;

    @Before
    public void setUp() {
        when(root.createAttribute(anyString())).thenReturn(attr);
        when(root.createAttributeNS(anyString(), anyString())).thenReturn(attr);
        when(root.createElement(anyString())).thenReturn(xml);
        when(root.createElementNS(anyString(), anyString())).thenReturn(xml);

        when(xml.getNodeType()).thenReturn(org.w3c.dom.Node.ELEMENT_NODE);
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
    public void testXmlNode() {
        assertThat(navigator.xml()).hasFieldOrPropertyWithValue("wrappedNode", xml);
    }

    @Test
    public void testRootNode() {
        assertThat(navigator.root()).hasFieldOrPropertyWithValue("wrappedNode", root);
    }

    @Test
    public void testParentOfRegularNode() {
        assertThat(navigator.parentOf(new DomNode(xml))).hasFieldOrPropertyWithValue("wrappedNode", root);
    }

    @Test
    public void testParentOfRootNode() {
        assertThat(navigator.parentOf(new DomNode(root))).isNull();
    }

    @Test
    public void testElementsOf() {
        assertThat(navigator.elementsOf(new DomNode(xml)))
                .extracting("wrappedNode", org.w3c.dom.Node.class)
                .containsExactly(child1, child2, child3);
    }

    @Test
    public void testAttributesOf() {
        assertThat(navigator.attributesOf(new DomNode(xml)))
                .extracting("wrappedNode", org.w3c.dom.Node.class)
                .containsExactly(child1, child2, child3);
    }

    @Test
    public void testCreateAttributeSuccess() {
        assertThat(navigator.createAttribute(new DomNode(xml), new QName("attr"))).isNotNull();
        verify(root).createAttribute("attr");
        verify(xml).setAttributeNode(attr);
    }

    @Test
    public void testCreateNsAttributeSuccess() {
        assertThat(navigator.createAttribute(new DomNode(xml), new QName("http://example.com/my", "attr"))).isNotNull();
        verify(root).createAttributeNS("http://example.com/my", "attr");
        verify(xml).setAttributeNode(attr);
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateAttributeFailureWhenParentIsNotAnElement() {
        navigator.createAttribute(new DomNode(root), new QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateAttributeFailure() {
        when(root.createAttribute(anyString())).thenThrow(DOMException.class);
        navigator.createAttribute(new DomNode(xml), new QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateNsAttributeFailure() {
        when(root.createAttributeNS(anyString(), anyString())).thenThrow(DOMException.class);
        navigator.createAttribute(new DomNode(xml), new QName("http://example.com/my", "attr"));
    }

    @Test
    public void testCreateElementSuccess() {
        assertThat(navigator.createElement(new DomNode(xml), new QName("elem"))).isNotNull();
        verify(root).createElement("elem");
        verify(xml).appendChild(xml);
    }

    @Test
    public void testCreateNsElementSuccess() {
        assertThat(navigator.createElement(new DomNode(xml), new QName("http://example.com/my", "elem"))).isNotNull();
        verify(root).createElementNS("http://example.com/my", "elem");
        verify(xml).appendChild(xml);
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateElementFailure() {
        when(root.createElement(anyString())).thenThrow(DOMException.class);
        navigator.createElement(new DomNode(xml), new QName("elem"));
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateNsElementFailure() {
        when(root.createElementNS(anyString(), anyString())).thenThrow(DOMException.class);
        navigator.createElement(new DomNode(xml), new QName("http://example.com/my", "elem"));
    }

    @Test
    public void testClone() {
        Node<org.w3c.dom.Node> result = navigator.clone(new DomNode(xml));
        assertThat(result).isEqualTo(new DomNode(xml));
    }

    @Test
    public void testSetTextSuccess() {
        navigator.setText(new DomNode(xml), "text");
        verify(xml).setTextContent("text");
    }

    @Test(expected = XmlBuilderException.class)
    public void testSetTextFailure() {
        doThrow(DOMException.class).when(xml).setTextContent(anyString());
        navigator.setText(new DomNode(xml), "text");
    }

    @Test
    public void testRemoveSuccess() {
        navigator.remove(new DomNode(xml));
        verify(root).removeChild(xml);
    }

    @Test(expected = XmlBuilderException.class)
    public void testRemoveFailure() {
        when(root.removeChild(any(org.w3c.dom.Node.class))).thenThrow(DOMException.class);
        navigator.remove(new DomNode(xml));
    }

}