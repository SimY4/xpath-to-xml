/*
 * Copyright 2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AxisStepExprTest {

  private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

  @Mock private Navigator<TestNode> navigator;
  @Mock private AxisResolver axisResolver;
  @Mock private Expr predicate1;
  @Mock private Expr predicate2;
  @Captor private ArgumentCaptor<NodeView<TestNode>> predicate1ViewCaptor;
  @Captor private ArgumentCaptor<NodeView<TestNode>> predicate2ViewCaptor;

  private StepExpr stepExpr;

  @BeforeEach
  void setUp() {
    when(axisResolver.resolveAxis(any(), any(), anyBoolean())).thenReturn(NodeSetView.empty());
    when(axisResolver.createAxisNode(any(), any(), anyInt()))
        .thenAnswer(
            AdditionalAnswers.answer(
                (Navigator<TestNode> nav, NodeView<TestNode> node, Integer pos) ->
                    new NodeView<>(node("node"), pos)));
    when(predicate1.resolve(any(), any(), anyBoolean())).thenReturn(BooleanView.of(false));
    when(predicate2.resolve(any(), any(), anyBoolean())).thenReturn(BooleanView.of(false));
    stepExpr = new AxisStepExpr(axisResolver, asList(predicate1, predicate2));
  }

  @Test
  @DisplayName(
      "When axis resolved in a list of child nodes should match nodes via predicates chain")
  void shouldMatchNodeViaPredicatesChainWhenAxisResolvedInListOfChildNodes() {
    // given
    when(axisResolver.resolveAxis(any(), any(), anyBoolean()))
        .thenReturn(new NodeView<>(node("node")));
    when(predicate1.resolve(any(), any(), anyBoolean())).thenReturn(BooleanView.of(true));
    when(predicate2.resolve(any(), any(), anyBoolean())).thenReturn(BooleanView.of(true));

    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isNotEmpty();
    verify(predicate1).resolve(eq(navigator), predicate1ViewCaptor.capture(), eq(false));
    verify(predicate2).resolve(eq(navigator), predicate2ViewCaptor.capture(), eq(false));
    assertThat(predicate1ViewCaptor.getValue()).extracting("position").containsExactly(1);
    assertThat(predicate2ViewCaptor.getValue()).extracting("position").containsExactly(1);
  }

  @Test
  @DisplayName("When predicate list is empty should return nodes resolved by axis")
  void shouldReturnNodesResolvedByStepExprOnly() {
    // given
    when(axisResolver.resolveAxis(any(), any(), anyBoolean()))
        .thenReturn(new NodeView<>(node("node")));
    stepExpr = new AxisStepExpr(axisResolver, Collections.emptyList());

    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isNotEmpty();
    verify(predicate1, never()).resolve(any(), any(), anyBoolean());
    verify(predicate2, never()).resolve(any(), any(), anyBoolean());
  }

  @Test
  @DisplayName("When traverse returns nothing should should short circuit resolve")
  void shouldShortCircuitWhenStepTraversalReturnsNothing() {
    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("When predicate traverse returns nothing should should short circuit resolve")
  void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
    // given
    when(axisResolver.resolveAxis(any(), any(), anyBoolean()))
        .thenReturn(new NodeView<>(node("node")));

    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isEmpty();
    verify(predicate2, never()).resolve(any(), any(), anyBoolean());
  }

  @Test
  @DisplayName("When only axis is resolvable should create node and resolve predicates")
  void shouldCreateNodeAndResolvePredicatesWhenOnlyAxisIsResolvable() {
    // given
    when(axisResolver.resolveAxis(any(), any(), eq(true))).thenReturn(new NodeView<>(node("node")));
    when(predicate1.resolve(any(), any(), eq(true))).thenReturn(BooleanView.of(true));
    when(predicate2.resolve(any(), any(), eq(true))).thenReturn(BooleanView.of(true));

    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, true);

    // then
    assertThat(result).isNotEmpty();
    InOrder inOrder = inOrder(predicate1, predicate2);
    inOrder.verify(predicate1).resolve(eq(navigator), predicate1ViewCaptor.capture(), eq(false));
    inOrder.verify(predicate1).resolve(eq(navigator), predicate1ViewCaptor.capture(), eq(true));
    inOrder.verify(predicate2).resolve(eq(navigator), predicate2ViewCaptor.capture(), eq(false));
    inOrder.verify(predicate2).resolve(eq(navigator), predicate2ViewCaptor.capture(), eq(true));
    assertThat(predicate1ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1), tuple(false, 2));
    assertThat(predicate2ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1), tuple(false, 1));
  }

  @Test
  @DisplayName(
      "When axis and predicates are partially resolvable should create new node and resolve predicates")
  void shouldCreateNodeAndResolvePredicatesWhenAxisAndPredicatesArePartiallyResolvable() {
    // given
    when(axisResolver.resolveAxis(any(), any(), eq(true))).thenReturn(new NodeView<>(node("node")));
    when(predicate1.resolve(any(), any(), anyBoolean())).thenReturn(BooleanView.of(true));
    when(predicate2.resolve(any(), any(), eq(true))).thenReturn(BooleanView.of(true));

    // when
    IterableNodeView<TestNode> result = stepExpr.resolve(navigator, parentNode, true);

    // then
    assertThat(result).isNotEmpty();
    InOrder inOrder = inOrder(predicate1, predicate2);
    inOrder.verify(predicate1).resolve(eq(navigator), predicate1ViewCaptor.capture(), eq(false));
    inOrder.verify(predicate2).resolve(eq(navigator), predicate2ViewCaptor.capture(), eq(false));
    inOrder.verify(predicate1).resolve(eq(navigator), predicate1ViewCaptor.capture(), eq(true));
    inOrder.verify(predicate2).resolve(eq(navigator), predicate2ViewCaptor.capture(), eq(true));
    assertThat(predicate1ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1), tuple(false, 2));
    assertThat(predicate2ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1), tuple(false, 2));
  }

  @Test
  @DisplayName("When unable to satisfy expression conditions should throw")
  void shouldThrowWhenUnableToSatisfyExpressionsConditions() {
    // given
    when(axisResolver.resolveAxis(any(), any(), eq(true))).thenReturn(new NodeView<>(node("node")));

    // when
    assertThatThrownBy(() -> stepExpr.resolve(navigator, parentNode, true))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testToString() {
    assertThat(stepExpr).hasToString(axisResolver.toString() + predicate1 + predicate2);
  }
}
