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

import java.util.Collections;
import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParentTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    private StepExpr parent;

    @Before
    public void setUp() {
        when(navigator.parentOf(node("node"))).thenReturn(node("parent"));

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

        parent = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnParentNodeOnTraverse() {
        Set<NodeWrapper<String>> result = parent.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);

        assertThat(result).containsExactly(node("parent"));
        verify(predicate1).apply(predicate1ContextCaptor.capture(), eq(node("parent")), eq(false));
        verify(predicate2).apply(predicate2ContextCaptor.capture(), eq(node("parent")), eq(false));
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        when(navigator.parentOf(node("node"))).thenReturn(null);

        Set<NodeWrapper<String>> result = parent.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);
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

        Set<NodeWrapper<String>> result = parent.apply(new ExprContext<String>(navigator, 3, 1), node("node"), false);
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        assertThat(predicate1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldSkipAppendingNodesForNodeThatIsNoLast() {
        when(navigator.parentOf(node("node"))).thenReturn(null);

        Set<NodeWrapper<String>> result = parent.apply(new ExprContext<String>(navigator, 3 ,1), node("node"), true);
        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        when(navigator.parentOf(node("node"))).thenReturn(null);

        parent.apply(new ExprContext<String>(navigator, 1 ,1), node("node"), true);
    }

    @Test
    public void testToString() {
        assertThat(parent).hasToString("..[" + predicate1 + "][" + predicate2 + ']');
    }

}