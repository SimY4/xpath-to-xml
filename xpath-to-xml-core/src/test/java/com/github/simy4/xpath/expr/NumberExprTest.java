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
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NumberExprTest {

  private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

  @Mock private Navigator<TestNode> navigator;

  private final Expr numberExpr = new NumberExpr(3.0);

  @Test
  @DisplayName("Should always return single number node")
  void shouldAlwaysReturnSingleNumberNode() {
    View<TestNode> result = numberExpr.resolve(navigator, parentNode, false);
    assertThat(result).extracting("number").isEqualTo(3.0);
  }

  @Test
  void testToString() {
    assertThat(numberExpr).hasToString("3.0");
  }
}
