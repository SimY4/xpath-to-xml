package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComparisonExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    private Expr comparisonExpr;

    @Before
    public void setUp() {
        comparisonExpr = new ComparisonExpr(leftExpr, rightExpr, Op.EQ);
    }

    @Test
    public void shouldReturnGivenXmlWhenTraversalOfLeftAndRightGivesTheSameValue() {
        when(leftExpr.apply(navigator, node("xml"), false)).thenReturn(singletonList(node("node1")));
        when(rightExpr.apply(navigator, node("xml"), false)).thenReturn(singletonList(node("node1")));

        List<NodeWrapper<String>> result = comparisonExpr.apply(navigator, node("xml"), false);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void shouldReturnGivenXmlWhenTraversalOfLeftAndRightGivesNothing() {
        List<NodeWrapper<String>> result = comparisonExpr.apply(navigator, node("xml"), false);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void shouldReturnEmptyListWhenTraversalOfLeftAndRightGivesDifferentValues() {
        when(leftExpr.apply(navigator, node("xml"), false)).thenReturn(singletonList(node("node1")));
        when(rightExpr.apply(navigator, node("xml"), false)).thenReturn(singletonList(node("node2")));

        List<NodeWrapper<String>> result = comparisonExpr.apply(navigator, node("xml"), false);
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldCreateLeftComparisonBranchDuringGreedyTraversal() {
        when(leftExpr.apply(navigator, node("xml"), true)).thenReturn(singletonList(node("node1")));

        List<NodeWrapper<String>> result = comparisonExpr.apply(navigator, node("xml"), true);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void shouldCreateLeftComparisonBranchAndSetRightComparisonBranchResultDuringGreedyTraversal() {
        when(leftExpr.apply(navigator, node("xml"), true)).thenReturn(singletonList(node("node1")));
        when(rightExpr.apply(navigator, node("xml"), false)).thenReturn(singletonList(node("node2")));

        List<NodeWrapper<String>> result = comparisonExpr.apply(navigator, node("xml"), true);
        assertThat(result).containsExactly(node("xml"));
    }

    @Test
    public void testToString() {
        assertThat(comparisonExpr).hasToString(leftExpr + "=" + rightExpr);
    }

}