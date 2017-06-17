package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.operators.Operator;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.ExprContextMatcher;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComparisonExprTest {

    private static final NodeView<String> parentNode = new NodeView<String>(node("node"));

    @Mock private Navigator<String> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;
    @Mock private Operator operator;
    @Captor private ArgumentCaptor<ExprContext<String>> leftExprContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> rightExprContextCaptor;

    private Expr comparisonExpr;

    @Before
    public void setUp() {
        when(operator.resolve(ArgumentMatchers.<View<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(BooleanView.<String>falsy());

        comparisonExpr = new ComparisonExpr(leftExpr, rightExpr, operator);
    }

    @Test
    public void shouldReturnGivenXmlWhenOpTestReturnsSucceeds() {
        // given
        when(operator.resolve(ArgumentMatchers.<View<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(BooleanView.<String>truthy());
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);
        context.advance();

        // when
        View<String> result = comparisonExpr.resolve(context, parentNode);

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(parentNode));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(parentNode));
        assertThat(result).isEqualTo(BooleanView.<String>truthy());
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
        View<String> result = comparisonExpr.resolve(context, parentNode);

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(parentNode));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(parentNode));
        assertThat(result).isEqualTo(BooleanView.<String>falsy());
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
        View<String> result = comparisonExpr.resolve(context, parentNode);

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture(), eq(parentNode));
        verify(rightExpr).resolve(rightExprContextCaptor.capture(), eq(parentNode));
        verify(operator, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<View<String>>any(), ArgumentMatchers.<View<String>>any());
        assertThat(result).isEqualTo(BooleanView.<String>falsy());
        assertThat(leftExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
        assertThat(rightExprContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test
    public void shouldCreateBothComparisonBranchesDuringGreedyResolutionWhenNeedyResolutionReturnsEmptySet() {
        // given
        when(leftExpr.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()), eq(parentNode)))
                .thenReturn(singleton(new NodeView<String>(node("node1"))));
        when(rightExpr.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()), eq(parentNode)))
                .thenReturn(singleton(new NodeView<String>(node("node2"))));
        ExprContext<String> context = new ExprContext<String>(navigator, true, 1);
        context.advance();

        // when
        View<String> result = comparisonExpr.resolve(context, parentNode);

        // then
        verify(leftExpr, times(2)).resolve(leftExprContextCaptor.capture(), eq(parentNode));
        verify(rightExpr, times(2)).resolve(rightExprContextCaptor.capture(), eq(parentNode));
        verify(operator).apply(ArgumentMatchers.<ExprContext<String>>any(),
                eq(singleton(new NodeView<String>(node("node1")))), eq(singleton(new NodeView<String>(node("node2")))));
        assertThat(result).isEqualTo(BooleanView.<String>truthy());
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
        assertThat(comparisonExpr).hasToString(leftExpr.toString() + operator.toString() + rightExpr.toString());
    }

}