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

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
class UnaryExprTest {

  static Stream<Arguments> number() {
    return Stream.of(
        arguments(new LiteralView<>("2.0")),
        arguments(new NumberView<>(2.0)),
        arguments(new NodeView<>(node("2.0"))),
        arguments(NodeSetView.of(singletonList(node("2.0")), node -> true)));
  }

  static Stream<Arguments> nan() {
    return Stream.of(
        arguments(new LiteralView<>("literal")),
        arguments(new NumberView<>(Double.NaN)),
        arguments(new NodeView<>(node("text"))),
        arguments(empty()));
  }

  @Mock private Navigator<TestNode> navigator;
  @Mock private Expr valueExpr;

  private Expr unaryExpr;

  @BeforeEach
  void setUp() {
    unaryExpr = new UnaryExpr(valueExpr);
  }

  @ParameterizedTest(name = "Given {0}")
  @DisplayName("Should negate number representation")
  @MethodSource("number")
  void shouldReturnNegatedNumberViewNode(View<Node> number) {
    // given
    when(valueExpr.resolve(any(), any(), anyBoolean())).thenReturn(number);

    // when
    assertThat(unaryExpr.resolve(navigator, new NodeView<>(node("node")), false))
        .extracting("number")
        .isEqualTo(-number.toNumber());
  }

  @ParameterizedTest(name = "Given {0}")
  @DisplayName("Should resolve to NaN")
  @MethodSource("nan")
  void negationWithNanShouldBeNan(View<Node> nan) {
    // given
    when(valueExpr.resolve(any(), any(), anyBoolean())).thenReturn(nan);

    // when
    assertThat(unaryExpr.resolve(navigator, new NodeView<>(node("node")), false))
        .extracting("number")
        .isEqualTo(Double.NaN);
  }

  @Test
  void testToString() {
    assertThat(unaryExpr).hasToString("-(" + valueExpr + ')');
  }
}
