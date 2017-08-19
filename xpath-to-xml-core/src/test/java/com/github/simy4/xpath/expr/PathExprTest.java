package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PathExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private StepExpr stepExpr1;
    @Mock private StepExpr stepExpr2;
    @Mock private StepExpr stepExpr3;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr1ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr2ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> stepExpr3ContextCaptor;

    private Expr pathExpr;

    @Before
    public void setUp() {
        pathExpr = new PathExpr(asList(stepExpr1, stepExpr2, stepExpr3));
    }

    @Test
    public void shouldTraverseStepsOneByOneToGetTheResultingList() {
        // given
        when(stepExpr1.resolve(stepExpr1ContextCaptor.capture())).thenReturn(new NodeView<>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(new NodeView<>(node("node3")));
        when(stepExpr3.resolve(stepExpr3ContextCaptor.capture())).thenReturn(new NodeView<>(node("node4")));

        // when
        View<TestNode> result = pathExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("node4"));
        assertThat(stepExpr1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
        assertThat(stepExpr2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
        assertThat(stepExpr3ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(tuple(navigator, false, false, 1));
    }

    @Test
    public void shouldShortCircuitNonGreedyTraversalWhenStepTraversalReturnsNothing() {
        // given
        when(stepExpr1.resolve(any(ViewContext.class)))
                .thenReturn(new NodeView<>(node("node2")));
        when(stepExpr2.resolve(stepExpr2ContextCaptor.capture())).thenReturn(NodeSetView.empty());

        // when
        View<TestNode> result = pathExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
        verify(stepExpr3, never()).resolve(any(ViewContext.class));
    }

    @Test
    public void testToString() {
        assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
    }

}