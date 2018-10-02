package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
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
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr1ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr2ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr3ContextCaptor;

    private PathExpr pathExpr;

    @BeforeEach
    void setUp() {
        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    @DisplayName("Should traverse steps one by one to get the resulting list")
    void shouldTraverseStepsOneByOneToGetTheResultingList() {
        // given
        when(stepExpr1.resolve(stepExpr1ContextCaptor.capture())).thenReturn(new NodeView<>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(new NodeView<>(node("node3")));
        when(stepExpr3.resolve(stepExpr3ContextCaptor.capture())).thenReturn(new NodeView<>(node("node4")));

        // when
        IterableNodeView<TestNode> result = pathExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).extracting("node").containsExactly(node("node4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
        assertThat(stepExpr2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
        assertThat(stepExpr3ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
    }

    @Test
    @DisplayName("When step traverse returns nothing should short circuit non greedy traversal")
    void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        // given
        when(stepExpr1.resolve(any())).thenReturn(new NodeView<>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(NodeSetView.empty());

        // when
        IterableNodeView<TestNode> result = pathExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isEmpty();
        verify(stepExpr3, never()).resolve(any());
    }

    @Test
    void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}