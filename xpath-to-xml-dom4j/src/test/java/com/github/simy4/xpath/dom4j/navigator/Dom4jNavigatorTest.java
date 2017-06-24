package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer1;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Dom4jNavigatorTest {

    @Mock private Document root;
    @Mock private Element parent;
    @Mock private Element xml;
    @Mock private Attribute attr;

    @Mock private Element child1;
    @Mock private Element child2;
    @Mock private Element child3;

    @Mock private Attribute attr1;
    @Mock private Attribute attr2;
    @Mock private Attribute attr3;


    private Navigator<org.dom4j.Node> navigator;

    @Before
    public void setUp() {
        when(xml.addAttribute(any(org.dom4j.QName.class), anyString()))
                .thenAnswer(AdditionalAnswers.answer((Answer1<Element, org.dom4j.QName>) qname -> {
                    when(xml.attribute(qname)).thenReturn(attr);
                    return xml;
                }));
        when(xml.addElement(any(org.dom4j.QName.class)))
                .thenAnswer(AdditionalAnswers.answer((Answer1<Element, org.dom4j.QName>) qname -> {
                    when(xml.element(qname)).thenReturn(child1);
                    return child1;
                }));

        when(root.getNodeType()).thenReturn(Node.DOCUMENT_NODE);
        when(root.getRootElement()).thenReturn(parent);

        when(xml.getDocument()).thenReturn(root);
        when(xml.getNodeType()).thenReturn(org.dom4j.Node.ELEMENT_NODE);
        when(xml.getParent()).thenReturn(parent);
        when(xml.createCopy()).thenReturn(xml);
        when(xml.elementIterator()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(xml.attributeIterator()).thenReturn(Arrays.asList(attr1, attr2, attr3).iterator());

        when(attr.getNodeType()).thenReturn(org.dom4j.Node.ATTRIBUTE_NODE);

        navigator = new Dom4jNavigator(xml);
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
        assertThat(navigator.parentOf(new Dom4jNode(xml))).hasFieldOrPropertyWithValue("wrappedNode", parent);
    }

    @Test
    public void testParentOfRootNode() {
        assertThat(navigator.parentOf(new Dom4jNode(root))).isNull();
    }

    @Test
    public void testElementsOfDocument() {
        assertThat(navigator.elementsOf(new Dom4jNode(root)))
                .extracting("wrappedNode", Element.class)
                .containsExactly(parent);
    }

    @Test
    public void testElementsOfElement() {
        assertThat(navigator.elementsOf(new Dom4jNode(xml)))
                .extracting("wrappedNode", Element.class)
                .containsExactly(child1, child2, child3);
    }

    @Test
    public void testElementsOfNonElement() {
        assertThat(navigator.elementsOf(new Dom4jNode(attr))).isEmpty();
    }

    @Test
    public void testAttributesOf() {
        assertThat(navigator.attributesOf(new Dom4jNode(xml)))
                .extracting("wrappedNode", Attribute.class)
                .containsExactly(attr1, attr2, attr3);
    }

    @Test
    public void testAttributesOfNonElementNode() {
        assertThat(navigator.attributesOf(new Dom4jNode(attr))).isEmpty();
    }

    @Test
    public void testCreateAttributeSuccess() {
        assertThat(navigator.createAttribute(new Dom4jNode(xml), new QName("attr")));
    }

    @Test
    public void testCreateNsAttributeSuccess() {
        assertThat(navigator.createAttribute(new Dom4jNode(xml), new QName("http://example.com/my", "attr", "my")));
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateAttributeFailure() {
        navigator.createAttribute(new Dom4jNode(root), new QName("attr"));
    }

    @Test
    public void testCreateElementSuccess() {
        assertThat(navigator.createElement(new Dom4jNode(xml), new QName("elem"))).isNotNull();
        verify(xml).addElement(new org.dom4j.QName("elem"));
    }

    @Test
    public void testCreateNsElementSuccess() {
        assertThat(navigator.createElement(new Dom4jNode(xml), new QName("http://example.com/my", "elem", "my"))).isNotNull();
        verify(xml).addElement(new org.dom4j.QName("elem", new Namespace("my", "http://example.com/my")));
    }

    @Test(expected = XmlBuilderException.class)
    public void testCreateElementFailure() {
        navigator.createElement(new Dom4jNode(attr), new QName("elem"));
    }

    @Test
    public void testPrependCopySuccess() {
        List<Element> elements = new ArrayList<>();
        elements.add(xml);
        when(parent.elements()).thenReturn(elements);

        navigator.prependCopy(new Dom4jNode(xml));
        verify(xml).createCopy();
        assertThat(elements).containsExactly(xml, xml);
    }

    @Test(expected = XmlBuilderException.class)
    public void testPrependCopyNoParent() {
        navigator.prependCopy(new Dom4jNode(DocumentHelper.createElement("elem")));
    }

    @Test(expected = XmlBuilderException.class)
    public void testPrependCopyFailure() {
        navigator.prependCopy(new Dom4jNode(root));
    }

    @Test
    public void testSetTextSuccess() {
        navigator.setText(new Dom4jNode(xml), "text");
        verify(xml).setText("text");
    }

    @Test(expected = XmlBuilderException.class)
    public void testSetTextFailure() {
        doThrow(UnsupportedOperationException.class).when(xml).setText(anyString());
        navigator.setText(new Dom4jNode(xml), "text");
    }

    @Test
    @Ignore
    public void testRemoveSuccess() {
        navigator.remove(new Dom4jNode(xml));
        verify(parent).remove(xml);
    }

    @Test(expected = XmlBuilderException.class)
    public void testRemoveFailure() {
        navigator.remove(new Dom4jNode(root));
    }

}