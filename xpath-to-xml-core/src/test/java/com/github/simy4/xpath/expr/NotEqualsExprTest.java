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

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
class NotEqualsExprTest extends AbstractOperationExprTest {

  static Stream<View<?>> equals() {
    return Stream.of(
        new LiteralView<>("2.0"),
        new NumberView<>(2.0),
        new NodeView<>(node("2.0")),
        NodeSetView.of(singletonList(node("2.0")), node -> true));
  }

  static Stream<Arguments> equalPairs() {
    return equals().flatMap(l -> equals().map(r -> arguments(l, r)));
  }

  static Stream<View<?>> nonEquals() {
    return Stream.of(
        new LiteralView<>("literal"),
        new NumberView<>(10.0),
        new NodeView<>(node("node")),
        BooleanView.of(false),
        empty());
  }

  static Stream<Arguments> notEqualPairs() {
    return equals().flatMap(l -> nonEquals().map(r -> arguments(l, r)));
  }

  private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

  @Mock private Navigator<TestNode> navigator;

  @BeforeEach
  void setUp() {
    operationExpr = new NotEqualsExpr(leftExpr, rightExpr);
  }

  @ParameterizedTest(name = "Given equal views, {0} and {1}")
  @DisplayName("Should resolve to false")
  @MethodSource("equalPairs")
  void shouldResolveEqualViewsToFalse(View<Node> left, View<Node> right) {
    // given
    when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
    when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

    // when
    var result = operationExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isEqualTo(BooleanView.of(false));
  }

  @ParameterizedTest(name = "Given non-equal views, {0} and {1}")
  @DisplayName("Should resolve to true")
  @MethodSource("notEqualPairs")
  void shouldResolveNonEqualViewsToTrue(View<Node> left, View<Node> right) {
    // given
    when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
    when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

    // when
    var result = operationExpr.resolve(navigator, parentNode, false);

    // then
    assertThat(result).isEqualTo(BooleanView.of(true));
  }

  @ParameterizedTest(name = "Given equal views, {0} and {1} and greedy context")
  @DisplayName("Should match and resolve to true")
  @MethodSource("equalPairs")
  void shouldApplyRightViewToLeftViewWhenShouldCreate(View<Node> left, View<Node> right) {
    assumeThat(left).isInstanceOf(IterableNodeView.class);
    assumeThat(((Iterable<?>) left)).isNotEmpty();

    // given
    when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
    when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

    // when
    var result = operationExpr.resolve(navigator, parentNode, true);

    // then
    assertThat(result).isEqualTo(BooleanView.of(true));
    var textCaptor = ArgumentCaptor.forClass(String.class);
    verify(navigator).setText(any(TestNode.class), textCaptor.capture());
    assertThat(textCaptor.getValue()).isNotEqualTo(right.toString());
  }

  @ParameterizedTest(name = "Given equal views, {0} and {1} and greedy context")
  @DisplayName("Should throw on resolve")
  @MethodSource("equalPairs")
  void shouldThrowWhenShouldCreate(View<Node> left, View<Node> right) {
    assumeThat(left).overridingErrorMessage("no iterables").isNotInstanceOf(IterableNodeView.class);
    // given
    when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
    when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

    // then
    assertThatThrownBy(() -> operationExpr.resolve(navigator, parentNode, true))
        .isInstanceOf(XmlBuilderException.class);
  }
}
