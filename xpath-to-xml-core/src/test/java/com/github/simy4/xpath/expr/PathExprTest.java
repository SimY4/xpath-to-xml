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
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

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
        when(stepExpr1.resolve(stepExpr1ContextCaptor.capture())).thenReturn(new NodeView<TestNode>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(new NodeView<TestNode>(node("node3")));
        when(stepExpr3.resolve(stepExpr3ContextCaptor.capture())).thenReturn(new NodeView<TestNode>(node("node4")));

        // when
        View<TestNode> result = pathExpr.resolve(new ExprContext<TestNode>(navigator, false, parentNode));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("node4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "position")
                .containsExactly(tuple(navigator, false, 0));
        assertThat(stepExpr2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "position")
                .containsExactly(tuple(navigator, false, 0));
        assertThat(stepExpr3ContextCaptor.getAllValues()).extracting("navigator", "greedy", "position")
                .containsExactly(tuple(navigator, false, 0));
    }

    @Test
    public void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        // given
        when(stepExpr1.resolve(ArgumentMatchers.<ExprContext<TestNode>>any()))
                .thenReturn(new NodeView<TestNode>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(NodeSetView.<TestNode>empty());

        // when
        View<TestNode> result = pathExpr.resolve(new ExprContext<TestNode>(navigator, false, parentNode));

        // then
        assertThat(result).isEqualTo(NodeSetView.empty());
        verify(stepExpr3, never()).resolve(ArgumentMatchers.<ExprContext<TestNode>>any());
    }

    @Test
    public void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}