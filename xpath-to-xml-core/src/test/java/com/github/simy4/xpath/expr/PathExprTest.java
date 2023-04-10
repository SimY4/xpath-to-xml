/*
 * Copyright 2017-2021 Alex Simkin
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

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;

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
    var result = pathExpr.resolve(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result).extracting("node").containsExactly(node("node31"));
    assertThat(stepExpr1ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1));
    assertThat(stepExpr2ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1));
    assertThat(stepExpr3ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
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
    var result = pathExpr.resolve(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result).isEmpty();
    assertThat(stepExpr1ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(false, 1));
    assertThat(stepExpr2ViewCaptor.getAllValues())
        .extracting("hasNext", "position")
        .containsExactly(tuple(true, 1), tuple(false, 2));
    verify(stepExpr3, never()).resolve(any(), any(), anyBoolean());
  }

  @Test
  void testToString() {
    assertThat(pathExpr).hasToString(stepExpr1 + "/" + stepExpr2 + "/" + stepExpr3);
  }
}
