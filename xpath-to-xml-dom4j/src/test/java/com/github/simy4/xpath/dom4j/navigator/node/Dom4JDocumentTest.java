package com.github.simy4.xpath.dom4j.navigator.node;

import com.github.simy4.xpath.XmlBuilderException;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class Dom4JDocumentTest {

    @Mock private Document document;
    @Mock private Element root;

    private Dom4jNode<Document> node;

    @Before
    public void setUp() {
        when(document.getText()).thenReturn("text");
        when(document.addElement(any(org.dom4j.QName.class))).thenReturn(root);

        node = new Dom4jDocument(document);
    }

    @Test
    public void shouldReturnEmptyListWhenObtainAttributes() {
        assertThat(node.attributes()).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListWhenObtainElementsFromEmptyDocument() {
        assertThat(node.elements()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnSingleRootNodeWhenObtainElements() {
        when(document.getRootElement()).thenReturn(root);
        assertThat(node.elements()).containsExactly(new Dom4jElement(root));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenCreateAttribute() {
        node.createAttribute(new org.dom4j.QName("attr"));
    }

    @Test
    public void shouldSetRootNodeWhenCreateElement() {
        assertThat(node.createElement(new org.dom4j.QName("elem"))).isEqualTo(new Dom4jElement(root));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowExceptionWhenRootElementAlreadyExist() {
        when(document.getRootElement()).thenReturn(root);
        node.createElement(new org.dom4j.QName("elem"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowExceptionOnGetName() {
        node.getName();
    }

    @Test
    public void shouldReturnNodeTextContent() {
        assertThat(node.getText()).isEqualTo("text");
    }

}