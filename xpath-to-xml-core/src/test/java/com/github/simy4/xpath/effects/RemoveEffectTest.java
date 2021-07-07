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
package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveEffectTest {

  @Mock private Navigator<TestNode> navigator;
  @Mock private Expr expr;

  private Effect removeEffect;

  @BeforeEach
  void setUp() {
    removeEffect = new RemoveEffect(expr);
  }

  @Test
  @DisplayName("Should detach resolved nodes")
  void shouldDetachResolvedNodes() {
    // given
    when(expr.resolve(any(), any(), anyBoolean())).thenReturn(new NodeView<>(node("node")));

    // when
    removeEffect.perform(navigator, node("xml"));

    // then
    verify(expr).resolve(eq(navigator), refEq(new NodeView<>(node("xml"))), eq(false));
    verify(navigator).remove(node("node"));
  }

  @Test
  @DisplayName("Should throw if resolved to a literal expr")
  void shouldThrowWhenResolvedToALiteralExpr() {
    // given
    LiteralView<Node> literal = new LiteralView<>("literal");
    when(expr.resolve(any(), any(), anyBoolean())).thenReturn(literal);

    // when
    assertThatThrownBy(() -> removeEffect.perform(navigator, node("xml")))
        .hasMessage("Failed to remove value into XML. Read-only view was resolved: " + literal);
  }

  @Test
  @DisplayName("When exception should propagate")
  void shouldPropagateOnException() {
    // given
    when(expr.resolve(any(), any(), anyBoolean())).thenReturn(new NodeView<>(node("node")));
    XmlBuilderException failure = new XmlBuilderException("Failure");
    doThrow(failure).when(navigator).remove(node("node"));

    // when
    assertThatThrownBy(() -> removeEffect.perform(navigator, node("xml"))).isSameAs(failure);
  }
}
