package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import com.github.simy4.xpath.utils.ExprContextMatcher;
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
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
            @Override
            public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                return singleton(node(invocationOnMock.getArgument(0) + "[P1]"));
            }
        });
        when(predicate2.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
            @Override
            public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                return singleton(node(invocationOnMock.getArgument(0) + "[P2]"));
            }
        });

        element = new Element(new QName("elem"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        // when
        Set<NodeWrapper<String>> result = element.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).containsExactly(node("elem"));
        verify(predicate1).resolve(predicate1ContextCaptor.capture(), eq(node("elem")));
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), eq(node("elem")));
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // given
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());

        // when
        Set<NodeWrapper<String>> result = element.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate1, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any()))
                .thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = element.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldSkipCreatinElementIfNodeIsNotLast() {
        // given
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any()))
                .thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = element.resolve(new ExprContext<String>(navigator, true, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldCreateElement() {
        // given
        when(predicate1.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>needyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(Collections.<NodeWrapper<String>>emptySet());
        when(navigator.createElement(new QName("elem"))).thenReturn(node("elem"));

        // when
        Set<NodeWrapper<String>> result = element.resolve(new ExprContext<String>(navigator, true, 1), node("node"));

        // then
        assertThat(result).containsExactly(node("elem"));
        verify(navigator).createElement(new QName("elem"));
        verify(predicate1, times(2)).resolve(predicate1ContextCaptor.capture(), eq(node("elem")));
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), eq(node("elem")));
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 1),
                        tuple(navigator, true, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, true, 1, 1));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        element = new Element(new QName("*", "attr"), Collections.<Expr>emptyList());

        // when
        element.resolve(new ExprContext<String>(navigator, true, 1), node("node"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        element = new Element(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        // when
        element.resolve(new ExprContext<String>(navigator, true, 1), node("node"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        // given
        when(navigator.elementsOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        when(navigator.createElement(any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        element.resolve(new ExprContext<String>(navigator, true, 1), node("node"));
    }

    @Test
    public void testToString() {
        assertThat(element).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

}