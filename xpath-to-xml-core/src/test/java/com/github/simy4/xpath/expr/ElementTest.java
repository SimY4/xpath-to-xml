package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
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
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    private StepExpr element;

    @Before
    public void setUp() {
        when(navigator.elementsOf(node("node")))
                .thenReturn(asList(node("elem"), node("another-elem")));

        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                anyBoolean())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
            @Override
            public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                return singleton(node(invocationOnMock.getArgument(0) + "[P1]"));
            }
        });
        when(predicate2.apply(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                anyBoolean())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
            @Override
            public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                return singleton(node(invocationOnMock.getArgument(0) + "[P2]"));
            }
        });

        element = new Element(new QName("elem"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        Set<NodeWrapper<String>> result = element.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);

        assertThat(result).containsExactly(node("elem"));
        verify(predicate1).apply(predicate1ContextCaptor.capture(), eq(node("elem")), eq(false));
        verify(predicate2).apply(predicate2ContextCaptor.capture(), eq(node("elem")), eq(false));
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());

        Set<NodeWrapper<String>> result = element.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);

        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(predicate1.apply(predicate1ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any(),
                eq(false))).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        Set<NodeWrapper<String>> result = element.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);

        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldSkipCreatinElementIfNodeIsNotLast() {
        when(predicate1.apply(predicate1ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any(),
                eq(false))).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        Set<NodeWrapper<String>> result = element.apply(new ExprContext<String>(navigator, 3, 1), node("node"), true);

        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldCreateElement() {
        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                eq(false))).thenReturn(Collections.<NodeWrapper<String>>emptySet());
        when(navigator.createElement(new QName("elem"))).thenReturn(node("elem"));

        Set<NodeWrapper<String>> result = element.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);

        assertThat(result).containsExactly(node("elem"));
        verify(navigator).createElement(new QName("elem"));
        verify(predicate1).apply(predicate1ContextCaptor.capture(), eq(node("elem")), eq(true));
        verify(predicate2).apply(predicate2ContextCaptor.capture(), eq(node("elem")), eq(true));
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        element = new Element(new QName("*", "attr"), Collections.<Expr>emptyList());

        element.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        element = new Element(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        element.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        when(navigator.createElement(any(QName.class))).thenThrow(XmlBuilderException.class);

        element.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test
    public void testToString() {
        assertThat(element).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

}