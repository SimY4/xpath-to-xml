package com.github.simy4.xpath.dom4j.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jAttribute;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jDocument;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jElement;
import com.github.simy4.xpath.dom4j.navigator.node.Dom4jNode;
import com.github.simy4.xpath.navigator.Navigator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class Dom4jNavigatorTest {

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


    private Navigator<Dom4jNode> navigator;

    @BeforeEach
    void setUp() {
        when(root.getNodeType()).thenReturn(Node.DOCUMENT_NODE);
        when(root.getRootElement()).thenReturn(parent);

        when(xml.getDocument()).thenReturn(root);
        when(xml.getNodeType()).thenReturn(org.dom4j.Node.ELEMENT_NODE);
        when(xml.getParent()).thenReturn(parent);
        when(xml.createCopy()).thenReturn(xml);
        when(xml.elementIterator()).thenReturn(Arrays.asList(child1, child2, child3).iterator());
        when(xml.attributeIterator()).thenReturn(Arrays.asList(attr1, attr2, attr3).iterator());

        navigator = new Dom4jNavigator(xml);
    }

    @Test
    void testRootNode() {
        assertThat(navigator.root()).hasFieldOrPropertyWithValue("node", root);
    }

    @Test
    void testParentOfRegularNode() {
        assertThat(navigator.parentOf(new Dom4jElement(xml))).hasFieldOrPropertyWithValue("node", parent);
    }

    @Test
    void testParentOfRootNode() {
        assertThat(navigator.parentOf(new Dom4jDocument(root))).isNull();
    }

    @Test
    void testElementsOfDocument() {
        assertThat(navigator.elementsOf(new Dom4jDocument(root)))
                .extracting("node", Element.class)
                .containsExactly(parent);
    }

    @Test
    void testElementsOfElement() {
        assertThat(navigator.elementsOf(new Dom4jElement(xml)))
                .extracting("node", Element.class)
                .containsExactly(child1, child2, child3);
    }

    @Test
    void testElementsOfNonElement() {
        assertThat(navigator.elementsOf(new Dom4jAttribute(attr))).isEmpty();
    }

    @Test
    void testAttributesOf() {
        assertThat(navigator.attributesOf(new Dom4jElement(xml)))
                .extracting("node", Attribute.class)
                .containsExactly(attr1, attr2, attr3);
    }

    @Test
    void testAttributesOfNonElementNode() {
        assertThat(navigator.attributesOf(new Dom4jAttribute(attr))).isEmpty();
    }

    @Test
    void testCreateAttributeSuccess() {
        assertThat(navigator.createAttribute(new Dom4jElement(xml), new QName("attr"))).isNotNull();
    }

    @Test
    void testCreateNsAttributeSuccess() {
        assertThat(navigator.createAttribute(new Dom4jElement(xml), new QName("http://example.com/my", "attr", "my"))).isNotNull();
    }

    @Test
    void testCreateAttributeFailure() {
        assertThatThrownBy(() -> navigator.createAttribute(new Dom4jDocument(root), new QName("attr")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testCreateElementSuccess() {
        assertThat(navigator.createElement(new Dom4jElement(xml), new QName("elem"))).isNotNull();
        verify(xml).addElement(new org.dom4j.QName("elem"));
    }

    @Test
    void testCreateNsElementSuccess() {
        assertThat(navigator.createElement(new Dom4jElement(xml), new QName("http://example.com/my", "elem", "my"))).isNotNull();
        verify(xml).addElement(new org.dom4j.QName("elem", new Namespace("my", "http://example.com/my")));
    }

    @Test
    void testCreateElementFailure() {
        assertThatThrownBy(() -> navigator.createElement(new Dom4jAttribute(attr), new QName("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testPrependCopySuccess() {
        var elements = new ArrayList<Element>();
        elements.add(xml);
        when(parent.elements()).thenReturn(elements);

        navigator.prependCopy(new Dom4jElement(xml));
        verify(xml).createCopy();
        assertThat(elements).containsExactly(xml, xml);
    }

    @Test
    void testPrependCopyNoParent() {
        assertThatThrownBy(() -> navigator.prependCopy(new Dom4jElement(DocumentHelper.createElement("elem"))))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testPrependCopyFailure() {
        assertThatThrownBy(() -> navigator.prependCopy(new Dom4jDocument(root)))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testSetTextSuccess() {
        navigator.setText(new Dom4jElement(xml), "text");
        verify(xml).setText("text");
    }

    @Test
    void testSetTextFailure() {
        doThrow(UnsupportedOperationException.class).when(xml).setText(anyString());
        assertThatThrownBy(() -> navigator.setText(new Dom4jElement(xml), "text"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testRemoveSuccess() {
        navigator.remove(new Dom4jElement(xml));
        verify(parent).remove((Node) xml);
    }

    @Test
    void testRemoveFailure() {
        assertThatThrownBy(() -> navigator.remove(new Dom4jDocument(root)))
                .isInstanceOf(XmlBuilderException.class);
    }

}