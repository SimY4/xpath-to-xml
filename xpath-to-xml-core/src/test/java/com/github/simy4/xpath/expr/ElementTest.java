package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElementTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;

    private StepExpr element;

    @Before
    public void setUp() {
        when(navigator.elementsOf(node("node")))
                .thenReturn(asList(node("elem"), node("another-elem"), node("elem")));

        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });

        element = new Element(new QName("elem"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        List<NodeWrapper<String>> result = element.traverse(navigator, singletonList(node("node")));
        assertThat(result).containsExactly(node("elem"), node("elem"));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = element.traverse(navigator, singletonList(node("node")));
        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), eq(false)))
                .thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = element.traverse(navigator, singletonList(node("node")));
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldCreateElement() {
        QName elementName = new QName("elem");
        NodeWrapper<String> wrappedElement = node("elem");
        when(navigator.createElement(elementName)).thenReturn(wrappedElement);

        NodeWrapper<String> newElement = element.createNode(navigator);
        assertThat(newElement).isEqualTo(wrappedElement);
        verify(navigator).createElement(elementName);
        verify(predicate1).apply(navigator, node("elem"), true);
        verify(predicate2).apply(navigator, node("elem"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        element = new Element(new QName("*", "attr"), Collections.<Expr>emptyList());

        element.createNode(navigator);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        element = new Element(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        element.createNode(navigator);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        when(navigator.createElement(any(QName.class))).thenThrow(XmlBuilderException.class);

        element.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(element).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

}