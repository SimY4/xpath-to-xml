/*
 * Copyright 2018-2021 Alex Simkin
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

import com.github.simy4.xpath.expr.axis.SelfAxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.namespace.QName;

import java.util.Collections;
import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredicateExprTest {

  static Stream<Arguments> truthy() {
    return Stream.of(
        arguments(new LiteralExpr("2.0")),
        arguments(new EqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0))),
        arguments(
            new AxisStepExpr(new SelfAxisResolver(new QName("*", "*")), Collections.emptySet())));
  }

  static Stream<Arguments> falsy() {
    return Stream.of(
        arguments(new LiteralExpr("")),
        arguments(new NotEqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0))));
  }

  @Mock private Navigator<TestNode> navigator;

  @ParameterizedTest(name = "Given truthy predicate {0}")
  @DisplayName("Should resolve to true")
  @MethodSource("truthy")
  void shouldReturnTrueForTruthyPredicate(Expr truthy) {
    // when
    boolean result =
        new PredicateExpr(truthy)
            .resolve(navigator, new NodeView<>(node("node")), false)
            .toBoolean();

    // then
    assertThat(result).isEqualTo(true);
  }

  @ParameterizedTest(name = "Given falsy predicate {0}")
  @DisplayName("Should resolve to false")
  @MethodSource("falsy")
  void shouldReturnFalseForNonGreedyFalsePredicate(Expr falsy) {
    // when
    boolean result =
        new PredicateExpr(falsy)
            .resolve(navigator, new NodeView<>(node("node")), false)
            .toBoolean();

    // then
    assertThat(result).isEqualTo(false);
  }

  @Test
  @DisplayName(
      "When greedy context, falsy predicate and new node should prepend missing nodes and return true")
  void shouldPrependMissingNodesAndReturnTrueOnGreedyFalsePredicateAndNewNode() {
    // given
    when(navigator.parentOf(node("node"))).thenReturn(node("parent"));
    when(navigator.createElement(node("parent"), new QName("node"))).thenReturn(node("node"));

    // when
    boolean result =
        new PredicateExpr(new NumberExpr(3.0))
            .resolve(navigator, new NodeView<>(node("node"), 1), true)
            .toBoolean();

    // then
    assertThat(result).isEqualTo(true);
    verify(navigator, times(2)).createElement(node("parent"), new QName("node"));
    verify(navigator, times(2)).appendPrev(node("node"), node("node"));
  }

  @Test
  void testToString() {
    // given
    Expr predicate = mock(Expr.class);

    // then
    assertThat(new PredicateExpr(predicate)).hasToString("[" + predicate + ']');
  }
}
