package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private StepExpr stepExpr1;
    @Mock private StepExpr stepExpr2;
    @Mock private StepExpr stepExpr3;
    @Captor private ArgumentCaptor<NodeView<TestNode>> stepExpr1ViewCaptor;
    @Captor private ArgumentCaptor<NodeView<TestNode>> stepExpr2ViewCaptor;
    @Captor private ArgumentCaptor<NodeView<TestNode>> stepExpr3ViewCaptor;

    private PathExpr pathExpr;

    @BeforeEach
    void setUp() {
        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    @DisplayName("Should traverse steps one by one to get the resulting list")
    void shouldTraverseStepsOneByOneToGetTheResultingList() {
        // given
        when(stepExpr1.resolve(eq(navigator), stepExpr1ViewCaptor.capture(), eq(false)))
                .thenReturn(new NodeView<>(node("node1")));
        when(stepExpr2.resolve(eq(navigator), stepExpr2ViewCaptor.capture(), eq(false)))
                .thenReturn(NodeSetView.of(asList(node("node21"), node("node22")), node -> true));
        when(stepExpr3.resolve(eq(navigator), stepExpr3ViewCaptor.capture(), eq(false)))
                .thenReturn(new NodeView<>(node("node31")));

        // when
        IterableNodeView<TestNode> result = pathExpr.resolve(navigator, parentNode, false);

        // then
        assertThat(result).extracting("node").containsExactly(node("node31"));
        assertThat(stepExpr1ViewCaptor.getAllValues()).extracting("hasNext", "position")
                .containsExactly(tuple(false, 1));
        assertThat(stepExpr2ViewCaptor.getAllValues()).extracting("hasNext", "position")
                .containsExactly(tuple(false, 1));
        assertThat(stepExpr3ViewCaptor.getAllValues()).extracting("hasNext", "position")
                .containsExactly(tuple(true, 1), tuple(false, 2));
    }

    @Test
    @DisplayName("When step traverse returns nothing should short circuit non greedy traversal")
    void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        // given
        when(stepExpr1.resolve(eq(navigator), stepExpr1ViewCaptor.capture(), eq(false)))
                .thenReturn(NodeSetView.of(asList(node("node11"), node("node12")), node -> true));
        when(stepExpr2.resolve(eq(navigator), stepExpr2ViewCaptor.capture(), eq(false)))
                .thenReturn(NodeSetView.empty());

        // when
        IterableNodeView<TestNode> result = pathExpr.resolve(navigator, parentNode, false);

        // then
        assertThat(result).isEmpty();
        assertThat(stepExpr1ViewCaptor.getAllValues()).extracting("hasNext", "position")
                .containsExactly(tuple(false, 1));
        assertThat(stepExpr2ViewCaptor.getAllValues()).extracting("hasNext", "position")
                .containsExactly(tuple(true, 1), tuple(false, 2));
        verify(stepExpr3, never()).resolve(any(), any(), anyBoolean());
    }

    @Test
    void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}