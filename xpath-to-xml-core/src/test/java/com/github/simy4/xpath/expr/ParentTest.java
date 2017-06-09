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

        parent = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnParentNodeOnTraverse() {
        // when
        Set<NodeWrapper<String>> result = parent.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).containsExactly(node("parent"));
        verify(predicate1).resolve(predicate1ContextCaptor.capture(), eq(node("parent")));
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), eq(node("parent")));
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // given
        when(navigator.parentOf(node("node"))).thenReturn(null);

        // when
        Set<NodeWrapper<String>> result = parent.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

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
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = parent.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldSkipAppendingNodesForNodeThatIsNoLast() {
        // given
        when(navigator.parentOf(node("node"))).thenReturn(null);

        // when
        Set<NodeWrapper<String>> result = parent.resolve(new ExprContext<String>(navigator, true, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate1, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // given
        when(navigator.parentOf(node("node"))).thenReturn(null);

        // when
        parent.resolve(new ExprContext<String>(navigator, true, 1), node("node"));
    }

    @Test
    public void testToString() {
        assertThat(parent).hasToString("..[" + predicate1 + "][" + predicate2 + ']');
    }

}