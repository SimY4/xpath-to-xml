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
package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;

import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import static java.util.stream.StreamSupport.stream;

class AncestorOrSelfAxisResolverTest extends AbstractAxisResolverTest {

  @BeforeEach
  void setUp() {
    axisResolver = new AncestorOrSelfAxisResolver(name, true);
  }

  @Test
  @DisplayName("When ancestor-or-self should return self and ancestor nodes")
  void shouldReturnSelfWithAllAncestorElements() {
    // given
    setUpResolvableAxis();
    axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

    // when
    var result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result)
        .extracting("node")
        .containsExactly(parentNode.getNode(), node("parent1"), node("parent2"), node(name));
  }

  @Test
  @DisplayName("When ancestor should return ancestor nodes")
  void shouldReturnOnlyAncestorElements() {
    // given
    setUpResolvableAxis();
    axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

    // when
    var result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result)
        .extracting("node")
        .containsExactly(node("parent1"), node("parent2"), node(name));
  }

  @Test
  @DisplayName("When ancestor-or-self and there are no ancestors should return self")
  void shouldReturnOnlySelfWhenThereAreNoAncestors() {
    // given
    doReturn(null).when(navigator).parentOf(parentNode.getNode());
    axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

    // when
    var result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
  }

  @Test
  @DisplayName("When ancestor and there are no ancestors should return empty")
  void shouldReturnEmptyWhenThereAreNoAncestors() {
    // given
    doReturn(null).when(navigator).parentOf(parentNode.getNode());
    axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

    // when
    var result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat((Iterable<?>) result).isEmpty();
  }

  @Test
  @DisplayName("Should throw on create node")
  @SuppressWarnings("ReturnValueIgnored")
  void shouldThrowOnCreateNode() {
    // when
    assertThatThrownBy(
            () ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                    .collect(Collectors.toList()))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testToString() {
    assertThat(axisResolver).hasToString("ancestor-or-self::" + name);
  }

  @Override
  void setUpResolvableAxis() {
    doReturn(node("parent1")).when(navigator).parentOf(parentNode.getNode());
    doReturn(node("parent2")).when(navigator).parentOf(node("parent1"));
    doReturn(node(name)).when(navigator).parentOf(node("parent2"));
  }
}
