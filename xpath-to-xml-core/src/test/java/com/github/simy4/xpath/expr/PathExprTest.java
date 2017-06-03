package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private StepExpr stepExpr1;
    @Mock private StepExpr stepExpr2;
    @Mock private StepExpr stepExpr3;

    private Expr pathExpr;

    @Before
    public void setUp() {
        when(stepExpr2.createNode(eq(navigator))).thenReturn(node("newNode2"));
        when(stepExpr3.createNode(eq(navigator))).thenReturn(node("newNode3"));

        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    public void shouldTraverseStepsOneByOneToGetTheResultingList() {
        when(stepExpr1.traverse(navigator, singletonList(node("node1")))).thenReturn(singletonList(node("node2")));
        when(stepExpr2.traverse(navigator, singletonList(node("node2")))).thenReturn(singletonList(node("node3")));
        when(stepExpr3.traverse(navigator, singletonList(node("node3")))).thenReturn(singletonList(node("node4")));

        List<NodeWrapper<String>> result = pathExpr.apply(navigator, node("node1"), false);
        assertThat(result).containsExactly(node("node4"));
    }

    @Test
    public void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        when(stepExpr1.traverse(navigator, singletonList(node("node1")))).thenReturn(singletonList(node("node2")));

        List<NodeWrapper<String>> result = pathExpr.apply(navigator, node("node1"), false);
        assertThat(result).isEmpty();
        verify(stepExpr3, never())
                .traverse(ArgumentMatchers.<Navigator<String>>any(), ArgumentMatchers.<NodeWrapper<String>>anyList());
    }

    @Test
    public void shouldCreateMissingNodesDuringGreedyTraversal() {
        when(stepExpr1.traverse(navigator, singletonList(node("node1")))).thenReturn(singletonList(node("node2")));

        List<NodeWrapper<String>> result = pathExpr.apply(navigator, node("node1"), true);
        assertThat(result).containsExactly(node("newNode3"));
        verify(navigator).append(node("node2"), node("newNode2"));
        verify(navigator).append(node("newNode2"), node("newNode3"));
    }

    @Test
    public void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}