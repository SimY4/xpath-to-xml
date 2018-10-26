package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AxisStepExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private AxisResolver axisResolver;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate2ContextCaptor;

    private StepExpr stepExpr;

    @BeforeEach
    void setUp() {
        when(axisResolver.resolveAxis(any())).thenReturn(NodeSetView.empty());
        when(axisResolver.createAxisNode(any())).thenReturn(new NodeView<>(node("node"), true));
        when(predicate1.resolve(any())).thenReturn(BooleanView.of(false));
        when(predicate2.resolve(any())).thenReturn(BooleanView.of(false));
        stepExpr = new AxisStepExpr(axisResolver, asList(predicate1, predicate2));
    }

    @Test
    @DisplayName("When axis resolved in a list of child nodes should match nodes via predicates chain")
    void shouldMatchNodeViaPredicatesChainWhenAxisResolvedInListOfChildNodes() {
        // given
        when(axisResolver.resolveAxis(any())).thenReturn(new NodeSetView<>(singleton(new NodeView<>(node("node")))));
        when(predicate1.resolve(any())).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(any())).thenReturn(BooleanView.of(true));

        // when
        var result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1).resolve(predicate1ContextCaptor.capture());
        verify(predicate2).resolve(predicate2ContextCaptor.capture());
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
    }

    @Test
    @DisplayName("When predicate list is empty should return nodes resolved by axis")
    void shouldReturnNodesResolvedByStepExprOnly() {
        // given
        when(axisResolver.resolveAxis(any())).thenReturn(new NodeSetView<>(singleton(new NodeView<>(node("node")))));
        stepExpr = new AxisStepExpr(axisResolver, Collections.emptyList());

        // when
        var result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1, never()).resolve(any());
        verify(predicate2, never()).resolve(any());
    }

    @Test
    @DisplayName("When traverse returns nothing should should short circuit resolve")
    void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // when
        var result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When predicate traverse returns nothing should should short circuit resolve")
    void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        when(axisResolver.resolveAxis(any())).thenReturn(new NodeSetView<>(singleton(new NodeView<>(node("node")))));

        // when
        var result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(any());
    }

    @Test
    @DisplayName("When step expr is partially resolvable should create node and resolve predicates")
    void shouldCreateNodeAndResolvePredicatesWhenStepExprIsPartiallyResolvable() {
        // given
        when(axisResolver.resolveAxis(argThat(ViewContext::isGreedy)))
                .thenReturn(new NodeSetView<>(singleton(new NodeView<>(node("node")))));
        when(predicate1.resolve(argThat(ViewContext::isGreedy))).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(argThat(ViewContext::isGreedy))).thenReturn(BooleanView.of(true));

        // when
        var result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, true));

        // then
        assertThat(result).isNotEmpty();
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

    @Test
    @DisplayName("When unable to satisfy expression conditions should throw")
    void shouldThrowWhenUnableToSatisfyExpressionsConditions() {
        // given
        when(axisResolver.resolveAxis(argThat(ViewContext::isGreedy)))
                .thenReturn(new NodeSetView<>(singleton(new NodeView<>(node("node")))));

        // when
        assertThatThrownBy(() -> stepExpr.resolve(new ViewContext<>(navigator, parentNode, true)))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(stepExpr).hasToString(axisResolver.toString() + predicate1 + predicate2);
    }

}
