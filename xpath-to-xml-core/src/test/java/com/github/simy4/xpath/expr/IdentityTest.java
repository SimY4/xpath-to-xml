package com.github.simy4.xpath.expr;

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
public class IdentityTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    private StepExpr identity;

    @Before
    public void setUp() {
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

        identity = new Identity(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnTheSameNodesOnResolve() {
        // given
        NodeWrapper<String> node = node("node");

        // when
        Set<NodeWrapper<String>> result = identity.resolve(new ExprContext<String>(navigator, false, 3), node);

        // then
        assertThat(result).containsExactly(node);
        verify(predicate1).resolve(predicate1ContextCaptor.capture(), eq(node("node")));
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), eq(node("node")));
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = identity.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void testToString() {
        assertThat(identity).hasToString(".[" + predicate1 + "][" + predicate2 + ']');
    }

}