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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComparisonExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;
    @Captor private ArgumentCaptor<ExprContext<String>> leftExprContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> rightExprContextCaptor;

    private Expr comparisonExpr;

    @Before
    public void setUp() {
        comparisonExpr = new ComparisonExpr(leftExpr, rightExpr, Op.EQ);
    }

    @Test
    public void shouldReturnGivenXmlWhenTraversalOfLeftAndRightGivesTheSameValue() {
        when(leftExpr.apply(leftExprContextCaptor.capture(), eq(node("xml")), eq(false)))
                .thenReturn(singleton(node("node1")));
        when(rightExpr.apply(rightExprContextCaptor.capture(), eq(node("xml")), eq(false)))
                .thenReturn(singleton(node("node1")));

        ExprContext<String> context = new ExprContext<String>(navigator, 3, 1);
        Set<NodeWrapper<String>> result = comparisonExpr.apply(context, node("xml"), false);
        assertThat(result).containsExactly(node("xml"));
        assertThat(leftExprContextCaptor.getValue()).isNotSameAs(context);
        assertThat(rightExprContextCaptor.getValue()).isNotSameAs(context);
    }

    @Test
    public void shouldReturnGivenXmlWhenTraversalOfLeftAndRightGivesNothing() {
        Set<NodeWrapper<String>> result = comparisonExpr.apply(new ExprContext<String>(navigator, 3, 1), node("xml"),
                false);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void shouldReturnEmptyListWhenTraversalOfLeftAndRightGivesDifferentValues() {
        when(leftExpr.apply(leftExprContextCaptor.capture(), eq(node("xml")), eq(false)))
                .thenReturn(singleton(node("node1")));
        when(rightExpr.apply(rightExprContextCaptor.capture(), eq(node("xml")), eq(false)))
                .thenReturn(singleton(node("node2")));

        ExprContext<String> context = new ExprContext<String>(navigator, 3, 1);
        Set<NodeWrapper<String>> result = comparisonExpr.apply(context, node("xml"), false);
        assertThat(result).isEmpty();
        assertThat(leftExprContextCaptor.getValue()).isNotSameAs(context);
        assertThat(rightExprContextCaptor.getValue()).isNotSameAs(context);
    }

    @Test
    public void shouldCreateLeftComparisonBranchDuringGreedyTraversal() {
        when(leftExpr.apply(ArgumentMatchers.<ExprContext<String>>any(), eq(node("xml")), eq(true)))
                .thenReturn(singleton(node("node1")));

        Set<NodeWrapper<String>> result = comparisonExpr.apply(new ExprContext<String>(navigator, 3, 1), node("xml"),
                true);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void shouldCreateLeftComparisonBranchAndSetRightComparisonBranchResultDuringGreedyTraversal() {
        when(leftExpr.apply(leftExprContextCaptor.capture(), eq(node("xml")), eq(true)))
                .thenReturn(singleton(node("node1")));
        when(rightExpr.apply(rightExprContextCaptor.capture(), eq(node("xml")), eq(false)))
                .thenReturn(singleton(node("node2")));

        ExprContext<String> context = new ExprContext<String>(navigator, 3, 1);
        Set<NodeWrapper<String>> result = comparisonExpr.apply(context, node("xml"), true);
        assertThat(result).containsExactly(node("xml"));
        assertThat(leftExprContextCaptor.getValue()).isNotSameAs(context);
        assertThat(rightExprContextCaptor.getValue()).isNotSameAs(context);
    }

    @Test
    public void testToString() {
        assertThat(comparisonExpr).hasToString(leftExpr + "=" + rightExpr);
    }

}