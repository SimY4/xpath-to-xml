package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AxisStepExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private AxisResolver axisResolver;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate2ContextCaptor;

    private StepExpr stepExpr;

    @Before
    public void setUp() {
        when(axisResolver.resolveAxis(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(NodeSetView.<TestNode>empty());
        when(axisResolver.createAxisNode(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(new NodeView<TestNode>(node("node"), true));
        when(predicate1.resolve(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(BooleanView.<TestNode>of(false));
        when(predicate2.resolve(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(BooleanView.<TestNode>of(false));
        stepExpr = new AxisStepExpr(axisResolver, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchNodeViaPredicatesChainFromAListOfChildNodes() {
        // given
        when(axisResolver.resolveAxis(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(new NodeSetView<TestNode>(singleton(new NodeView<TestNode>(node("node")))));
        when(predicate1.resolve(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(BooleanView.<TestNode>of(true));
        when(predicate2.resolve(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(BooleanView.<TestNode>of(true));

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1).resolve(predicate1ContextCaptor.capture());
        verify(predicate2).resolve(predicate2ContextCaptor.capture());
        assertThat((Object) predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
        assertThat((Object) predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
    }

    @Test
    public void shouldReturnNodesResolvedByStepExprOnly() {
        // given
        when(axisResolver.resolveAxis(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(new NodeSetView<TestNode>(singleton(new NodeView<TestNode>(node("node")))));
        stepExpr = new AxisStepExpr(axisResolver, Collections.<Expr>emptyList());

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, never()).resolve(ArgumentMatchers.<ViewContext<TestNode>>any());
        verify(predicate2, never()).resolve(ArgumentMatchers.<ViewContext<TestNode>>any());
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        when(axisResolver.resolveAxis(ArgumentMatchers.<ViewContext<TestNode>>any()))
                .thenReturn(new NodeSetView<TestNode>(singleton(new NodeView<TestNode>(node("node")))));

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ViewContext<TestNode>>any());
    }

    @Test
    public void shouldCreateNodeAndResolvePredicatesWhenStepExprIsPartiallyResolvable() {
        // given
        when(axisResolver.resolveAxis(argThat(greedyContext())))
                .thenReturn(new NodeSetView<Node>(singleton(new NodeView<Node>(node("node")))));
        when(predicate1.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, times(2)).resolve(predicate1ContextCaptor.capture());
        verify(predicate2, times(2)).resolve(predicate2ContextCaptor.capture());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 2));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 1));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowWhenUnableToSatisfyExpressionsConditions() {
        // given
        when(axisResolver.resolveAxis(argThat(greedyContext())))
                .thenReturn(new NodeSetView<Node>(singleton(new NodeView<Node>(node("node")))));

        // when
        stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true));
    }

    @Test
    public void testToString() {
        assertThat(stepExpr).hasToString(axisResolver.toString() + predicate1 + predicate2);
    }

    private <N extends Node> ArgumentMatcher<ViewContext<N>> greedyContext() {
        return new ArgumentMatcher<ViewContext<N>>() {
            @Override
            public boolean matches(ViewContext<N> context) {
                return context.isGreedy();
            }
        };
    }

}
