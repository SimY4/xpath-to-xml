package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeSetView;
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

import static com.github.simy4.xpath.utils.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathExprTest {

    @Mock private Navigator<TestNode> navigator;
    @Mock private StepExpr stepExpr1;
    @Mock private StepExpr stepExpr2;
    @Mock private StepExpr stepExpr3;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> stepExpr1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> stepExpr2ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> stepExpr3ContextCaptor;

    private Expr pathExpr;

    @Before
    public void setUp() {
        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    public void shouldTraverseStepsOneByOneToGetTheResultingList() {
        // given
        when(stepExpr1.resolve(stepExpr1ContextCaptor.capture(), eq(singleton(new NodeView<TestNode>(node("node1"))))))
                .thenReturn(singleton(new NodeView<TestNode>(node("node2"))));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture(), eq(singleton(new NodeView<TestNode>(node("node2"))))))
                .thenReturn(singleton(new NodeView<TestNode>(node("node3"))));
        when(stepExpr3.resolve(stepExpr3ContextCaptor.capture(), eq(singleton(new NodeView<TestNode>(node("node3"))))))
                .thenReturn(singleton(new NodeView<TestNode>(node("node4"))));

        // when
        View<TestNode> result = pathExpr.resolve(new ExprContext<TestNode>(navigator, false, 1),
                singleton(new NodeView<TestNode>(node("node1"))));

        // then
        assertThat(result).isEqualTo(singleton(new NodeView<TestNode>(node("node4"))));
        assertThat(stepExpr1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, false, 1, 0));
        assertThat(stepExpr2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, false, 1, 0));
        assertThat(stepExpr3ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, false, 1, 0));
    }

    @Test
    public void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        // given
        when(stepExpr1.resolve(ArgumentMatchers.<ExprContext<TestNode>>any(),
                eq(singleton(new NodeView<TestNode>(node("node1"))))))
                .thenReturn(singleton(new NodeView<TestNode>(node("node2"))));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture(), eq(singleton(new NodeView<TestNode>(node("node2"))))))
                .thenReturn(NodeSetView.<TestNode>empty());

        // when
        View<TestNode> result = pathExpr.resolve(new ExprContext<TestNode>(navigator, false, 1),
                singleton(new NodeView<TestNode>(node("node1"))));

        // then
        assertThat(result).isEqualTo(NodeSetView.empty());
        verify(stepExpr3, never()).resolve(ArgumentMatchers.<ExprContext<TestNode>>any(),
                ArgumentMatchers.<NodeView<TestNode>>any());
    }

    @Test
    public void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}