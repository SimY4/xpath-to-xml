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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Dom4jNavigatorTest {

    private Element parent = DocumentHelper.createElement(new org.dom4j.QName("parent"));
    private Document root = DocumentHelper.createDocument(parent);
    private Element xml = DocumentHelper.createElement(new org.dom4j.QName("xml"));
    private Attribute attr = DocumentHelper.createAttribute(xml, "attr", "");

    private Element child1 = DocumentHelper.createElement(new org.dom4j.QName("child1"));
    private Element child2 = DocumentHelper.createElement(new org.dom4j.QName("child2"));
    private Element child3 = DocumentHelper.createElement(new org.dom4j.QName("child3"));

    private Attribute attr1 = DocumentHelper.createAttribute(xml, "attr1", "");
    private Attribute attr2 = DocumentHelper.createAttribute(xml, "attr2", "");
    private Attribute attr3 = DocumentHelper.createAttribute(xml, "attr3", "");


    private Navigator<Dom4jNode> navigator;

    @BeforeEach
    void setUp() {
        parent.add(xml);
        xml.add(child1);
        xml.add(child2);
        xml.add(child3);
        xml.add(attr1);
        xml.add(attr2);
        xml.add(attr3);

        navigator = new Dom4jNavigator(new Dom4jDocument(root));
    }

    @Test
    void testRootNode() {
        assertThat(navigator.root()).isEqualTo(new Dom4jDocument(root));
    }

    @Test
    void testParentOfRegularNode() {
        assertThat(navigator.parentOf(new Dom4jElement(xml))).isEqualTo(new Dom4jElement(parent));
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
        Dom4jNode elem = navigator.createElement(new Dom4jElement(xml), new QName("elem"));
        assertThat(elem).extracting("name").containsExactly(new QName("elem"));
    }

    @Test
    void testCreateNsElementSuccess() {
        Dom4jNode elem = navigator.createElement(new Dom4jElement(xml), new QName("http://example.com/my", "elem", "my"));
        assertThat(elem).extracting("name").containsExactly(
                new QName("http://example.com/my", "elem", "my"));
    }

    @Test
    void testCreateElementFailure() {
        assertThatThrownBy(() -> navigator.createElement(new Dom4jAttribute(attr), new QName("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testPrependCopySuccess() {
        navigator.prependCopy(new Dom4jElement(xml));
        assertThat(parent.elements()).extracting("name").containsExactly(xml.getName(), xml.getName());
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
        assertThat(xml.getText()).isEqualTo("text");
    }

    @Test
    void testSetTextFailure() {
        assertThatThrownBy(() -> navigator.setText(new Dom4jDocument(root), "text"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testRemoveSuccess() {
        navigator.remove(new Dom4jElement(xml));
        assertThat(parent.elements()).doesNotContain(xml);
    }

    @Test
    void testRemoveFailure() {
        assertThatThrownBy(() -> navigator.remove(new Dom4jDocument(root)))
                .isInstanceOf(XmlBuilderException.class);
    }

}