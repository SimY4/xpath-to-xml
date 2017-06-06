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
public class AttributeTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    private StepExpr attribute;

    @Before
    public void setUp() {
        when(navigator.attributesOf(node("node")))
                .thenReturn(asList(node("attr"), node("another-attr")));

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

        attribute = new Attribute(new QName("attr"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        Set<NodeWrapper<String>> result = attribute.apply(new ExprContext<String>(navigator, 3, 1), node("node"),
                false);

        assertThat(result).containsExactly(node("attr"));
        verify(predicate1).apply(predicate1ContextCaptor.capture(), eq(node("attr")), eq(false));
        verify(predicate2).apply(predicate2ContextCaptor.capture(), eq(node("attr")), eq(false));
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        when(navigator.attributesOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());

        Set<NodeWrapper<String>> result = attribute.apply(new ExprContext<String>(navigator, 3, 1), node("node"),
                false);
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

        Set<NodeWrapper<String>> result = attribute.apply(new ExprContext<String>(navigator, 3, 1), node("node"),
                false);
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldSkipCreatinElementIfNodeIsNotLast() {
        when(predicate1.apply(predicate1ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any(),
                eq(false))).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        Set<NodeWrapper<String>> result = attribute.apply(new ExprContext<String>(navigator, 3, 1), node("node"), true);

        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldCreateAttribute() {
        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                eq(false))).thenReturn(Collections.<NodeWrapper<String>>emptySet());
        when(navigator.createAttribute(new QName("attr"))).thenReturn(node("attr"));

        Set<NodeWrapper<String>> result = attribute.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);

        assertThat(result).containsExactly(node("attr"));
        verify(navigator).createAttribute(new QName("attr"));
        verify(predicate1).apply(predicate1ContextCaptor.capture(), eq(node("attr")), eq(true));
        verify(predicate2).apply(predicate2ContextCaptor.capture(), eq(node("attr")), eq(true));
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        when(navigator.attributesOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        attribute = new Attribute(new QName("*", "attr"), Collections.<Expr>emptyList());

        attribute.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        when(navigator.attributesOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        attribute = new Attribute(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        attribute.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        when(navigator.attributesOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());
        when(navigator.createAttribute(any(QName.class))).thenThrow(XmlBuilderException.class);

        attribute.apply(new ExprContext<String>(navigator, 1, 1), node("node"), true);
    }

    @Test
    public void testToString() {
        assertThat(attribute).hasToString("@attr[" + predicate1 + "][" + predicate2 + ']');
    }

}