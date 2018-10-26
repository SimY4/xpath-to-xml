package com.github.simy4.xpath.xom.navigator;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.xom.navigator.node.XomAttribute;
import com.github.simy4.xpath.xom.navigator.node.XomDocument;
import com.github.simy4.xpath.xom.navigator.node.XomElement;
import com.github.simy4.xpath.xom.navigator.node.XomNode;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XomNavigatorTest {

    private final Element parent = new Element("parent");
    private final Document root = new Document(parent);
    private final Element xml = new Element("elem");

    private final Attribute attr1 = new Attribute("attr1", "text");
    private final Attribute attr2 = new Attribute("attr2", "text");
    private final Attribute attr3 = new Attribute("attr3", "text");
    private final Element child1 = new Element("child1");
    private final Element child2 = new Element("child2");
    private final Element child3 = new Element("child3");

    private Navigator<XomNode<?>> navigator;

    @BeforeEach
    void setUp() {
        parent.appendChild(xml);

        xml.appendChild(child1);
        xml.appendChild(child2);
        xml.appendChild(child3);
        xml.addAttribute(attr1);
        xml.addAttribute(attr2);
        xml.addAttribute(attr3);

        navigator = new XomNavigator(xml);
    }

    @Test
    void testRootNode() {
        assertThat(navigator.root()).hasFieldOrPropertyWithValue("node", root);
    }

    @Test
    void testParentOfRegularNode() {
        assertThat(navigator.parentOf(new XomElement(xml))).hasFieldOrPropertyWithValue("node", parent);
    }

    @Test
    void testParentOfRootNode() {
        assertThat(navigator.parentOf(new XomDocument(root))).isNull();
    }

    @Test
    void testElementsOfDocument() {
        assertThat(navigator.elementsOf(new XomDocument(root)))
                .extracting("node", Element.class)
                .containsExactly(parent);
    }

    @Test
    void testElementsOfElement() {
        assertThat(navigator.elementsOf(new XomElement(xml)))
                .extracting("node", Element.class)
                .containsExactly(child1, child2, child3);
    }

    @Test
    void testElementsOfNonElement() {
        assertThat(navigator.elementsOf(new XomAttribute(new Attribute("attr", "")))).isEmpty();
    }

    @Test
    void testAttributesOf() {
        assertThat(navigator.attributesOf(new XomElement(xml)))
                .extracting("node", Attribute.class)
                .containsExactly(attr1, attr2, attr3);
    }

    @Test
    void testAttributesOfNonElementNode() {
        assertThat(navigator.attributesOf(new XomDocument(root))).isEmpty();
    }

    @Test
    void testCreateAttributeSuccess() {
        assertThat(xml.getAttribute("attr")).isNull();
        assertThat(navigator.createAttribute(new XomElement(xml), new QName("attr"))).isNotNull();
        assertThat(xml.getAttribute("attr")).isNotNull();
    }

    @Test
    void testCreateAttributeFailure() {
        assertThatThrownBy(() -> navigator.createAttribute(new XomDocument(root), new QName("attr")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testCreateElementSuccess() {
        assertThat(xml.getFirstChildElement("elem")).isNull();
        assertThat(navigator.createElement(new XomElement(xml), new QName("elem"))).isNotNull();
        assertThat(xml.getFirstChildElement("elem")).isNotNull();
    }

    @Test
    void testCreateElementFailure() {
        assertThatThrownBy(() -> navigator.createElement(new XomAttribute(new Attribute("attr", "")),
                new QName("elem")))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testPrependCopySuccess() {
        navigator.prependCopy(new XomElement(xml));
        var childElements = parent.getChildElements();
        assertThat(childElements.size()).isEqualTo(2);
    }

    @Test
    void testPrependCopyNoParent() {
        assertThatThrownBy(() -> navigator.prependCopy(new XomElement(new Element("elem"))))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testPrependCopyNotAnElement() {
        assertThatThrownBy(() -> navigator.prependCopy(new XomDocument(root)))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testSetTextSuccess() {
        navigator.setText(new XomAttribute(attr1), "text");
        assertThat(attr1.getValue()).isEqualTo("text");
    }

    @Test
    void testSetTextFailure() {
        assertThatThrownBy(() -> navigator.setText(new XomDocument(root), "text"))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testRemoveSuccess() {
        navigator.remove(new XomElement(xml));
        assertThat(parent.getChildElements().size()).isEqualTo(0);
    }

}