package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.op.Op;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.utils.ExprContextMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComparisonExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;
    @Mock private Op op;
    @Captor private ArgumentCaptor<ExprContext<String>> leftExprContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> rightExprContextCaptor;

    private Expr comparisonExpr;

    @Before
    public void setUp() {
        comparisonExpr = new ComparisonExpr(leftExpr, rightExpr, op);
    }

    @Test
    public void shouldReturnGivenXmlWhenOpTestReturnsSucceeds() {
        // given
        when(op.test(ArgumentMatchers.<NodeView<String>>anySet(), ArgumentMatchers.<NodeView<String>>anySet()))
                .thenReturn(true);
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);
        context.advance();

        // when
        Set<NodeView<String>> result = comparisonExpr.resolve(context, node("xml"));

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(node("xml")));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(node("xml")));
        assertThat(result).containsExactly(node("xml"));
        assertThat(leftExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
        assertThat(rightExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test
    public void shouldReturnEmptySetWhenOpTestReturnsFails() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);
        context.advance();

        // when
        Set<NodeView<String>> result = comparisonExpr.resolve(context, node("xml"));

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(node("xml")));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(node("xml")));
        assertThat(result).isEmpty();
        assertThat(leftExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
        assertThat(rightExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test
    public void shouldReturnEmptySetWhenOpTestReturnsFailsAndExprShouldNotCreate() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, true, 3);
        context.advance();

        // when
        Set<NodeView<String>> result = comparisonExpr.resolve(context, node("xml"));

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(node("xml")));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(node("xml")));
        verify(op, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeView<String>>anySet(), ArgumentMatchers.<NodeView<String>>anySet());
        assertThat(result).isEmpty();
        assertThat(leftExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
        assertThat(rightExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test
    public void shouldCreateBothComparisonBranchesDuringGreedyResolutionWhenNeedyResolutionReturnsEmptySet() {
        // given
        when(leftExpr.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()), eq(node("xml"))))
                .thenReturn(singleton(node("node1")));
        when(rightExpr.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()), eq(node("xml"))))
                .thenReturn(singleton(node("node2")));
        ExprContext<String> context = new ExprContext<String>(navigator, true, 1);
        context.advance();

        // when
        Set<NodeView<String>> result = comparisonExpr.resolve(context, node("xml"));

        // then
        verify(leftExpr, times(2)).resolve(leftExprContextCaptor.capture(), eq(node("xml")));
        verify(rightExpr, times(2)).resolve(rightExprContextCaptor.capture(), eq(node("xml")));
        verify(op).apply(navigator, singleton(node("node1")), singleton(node("node2")));
        assertThat(result).containsExactly(node("xml"));
        assertThat(leftExprContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 0),
                        tuple(navigator, true, 1, 0));
        assertThat(rightExprContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 0),
                        tuple(navigator, true, 1, 0));
    }

    @Test
    public void testToString() {
        assertThat(comparisonExpr).hasToString(leftExpr.toString() + op.toString() + rightExpr.toString());
    }

}