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

import java.util.LinkedHashSet;
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
public class PathExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private StepExpr stepExpr1;
    @Mock private StepExpr stepExpr2;
    @Mock private StepExpr stepExpr3;
    @Captor private ArgumentCaptor<ExprContext<String>> stepExpr1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> stepExpr2ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> stepExpr3ContextCaptor;

    private Expr pathExpr;

    @Before
    public void setUp() {
        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    public void shouldTraverseStepsOneByOneToGetTheResultingList() {
        when(stepExpr1.apply(stepExpr1ContextCaptor.capture(), eq(node("node1")), eq(false)))
                .thenReturn(singleton(node("node2")));
        when(stepExpr2.apply(stepExpr2ContextCaptor.capture(), eq(node("node2")), eq(false)))
                .thenReturn(singleton(node("node3")));
        when(stepExpr3.apply(stepExpr3ContextCaptor.capture(), eq(node("node3")), eq(false)))
                .thenReturn(singleton(node("node4")));

        Set<NodeWrapper<String>> result = pathExpr.apply(new ExprContext<String>(navigator, 1, 1), node("node1"), false);
        assertThat(result).containsExactly(node("node4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr3ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldDeduplicateEqualNodesOnTraverse() {
        when(stepExpr1.apply(stepExpr1ContextCaptor.capture(), eq(node("node1")), eq(false)))
                .thenReturn(new LinkedHashSet<NodeWrapper<String>>(asList(node("node21"), node("node22"))));
        when(stepExpr2.apply(stepExpr2ContextCaptor.capture(), eq(node("node21")), eq(false)))
                .thenReturn(singleton(node("node3")));
        when(stepExpr2.apply(stepExpr2ContextCaptor.capture(), eq(node("node22")), eq(false)))
                .thenReturn(singleton(node("node3")));
        when(stepExpr3.apply(stepExpr3ContextCaptor.capture(), eq(node("node3")), eq(false)))
                .thenReturn(singleton(node("node4")));

        Set<NodeWrapper<String>> result = pathExpr.apply(new ExprContext<String>(navigator, 1, 1), node("node1"), false);
        assertThat(result).containsExactly(node("node4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr2ContextCaptor.getAllValues())
                .containsExactly(new ExprContext<String>(navigator, 1, 1), new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr3ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        when(stepExpr1.apply(stepExpr1ContextCaptor.capture(), eq(node("node1")), eq(false)))
                .thenReturn(singleton(node("node2")));

        Set<NodeWrapper<String>> result = pathExpr.apply(new ExprContext<String>(navigator, 1, 1), node("node1"), false);
        assertThat(result).isEmpty();
        verify(stepExpr3, never()).traverse(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldCreateMissingNodesDuringGreedyTraversal() {
        when(stepExpr1.apply(stepExpr1ContextCaptor.capture(), eq(node("node1")), eq(true)))
                .thenReturn(singleton(node("newNode2")));
        when(stepExpr2.apply(stepExpr2ContextCaptor.capture(), eq(node("newNode2")), eq(true)))
                .thenReturn(singleton(node("newNode3")));
        when(stepExpr3.apply(stepExpr3ContextCaptor.capture(), eq(node("newNode3")), eq(true)))
                .thenReturn(singleton(node("newNode4")));

        Set<NodeWrapper<String>> result = pathExpr.apply(new ExprContext<String>(navigator, 1, 1), node("node1"), true);
        assertThat(result).containsExactly(node("newNode4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr2ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
        assertThat(stepExpr3ContextCaptor.getAllValues()).containsExactly(new ExprContext<String>(navigator, 1, 1));
    }

    @Test
    public void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}