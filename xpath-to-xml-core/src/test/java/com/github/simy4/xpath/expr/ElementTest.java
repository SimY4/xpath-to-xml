package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.namespace.QName;
import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElementTest {

    @Mock
    private Navigator<String> navigator;

    private final StepExpr element = new Element(new QName("elem"));

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        when(navigator.elementsOf(node("node")))
                .thenReturn(asList(node("elem"), node("another-elem"), node("elem")));

        List<NodeWrapper<String>> result = element.traverse(navigator, singletonList(node("node")));
        assertThat(result).containsExactly(node("elem"), node("elem"));
    }

    @Test
    public void shouldCreateElement() {
        QName elementName = new QName("elem");
        NodeWrapper<String> wrappedElement = node("elem");
        when(navigator.createElement(elementName)).thenReturn(wrappedElement);

        NodeWrapper<String> newElement = element.createNode(navigator);
        assertThat(newElement).isEqualTo(wrappedElement);
        verify(navigator).createElement(elementName);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        when(navigator.createElement(any(QName.class))).thenThrow(XmlBuilderException.class);

        element.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(element).hasToString("elem");
    }

}