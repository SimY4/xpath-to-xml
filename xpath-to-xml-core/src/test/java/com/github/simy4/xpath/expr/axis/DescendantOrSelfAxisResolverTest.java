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
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.namespace.QName;

import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.StreamSupport.stream;

@MockitoSettings(strictness = Strictness.LENIENT)
class DescendantOrSelfAxisResolverTest extends AbstractAxisResolverTest {

  @BeforeEach
  void setUp() {
    when(navigator.createElement(any(TestNode.class), eq(name)))
        .thenReturn(node(name.getLocalPart()));

    axisResolver = new DescendantOrSelfAxisResolver(name, true);
  }

  @Test
  @DisplayName("When descendant-or-self should return self and descendant nodes")
  void shouldReturnSelfWithAllDescendantElements() {
    // given
    setUpResolvableAxis();
    axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat(result)
        .extracting("node")
        .containsExactly(
            parentNode.getNode(),
            node("node11"),
            node("node12"),
            node("node1111"),
            node("node1112"),
            node("node1211"),
            node("node1212"),
            node(name));
  }

  @Test
  @DisplayName("When descendant should return descendant nodes")
  void shouldReturnOnlyDescendantElements() {
    // given
    setUpResolvableAxis();
    axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat(result)
        .extracting("node")
        .containsExactly(
            node("node11"),
            node("node12"),
            node("node1111"),
            node("node1112"),
            node("node1211"),
            node("node1212"),
            node(name));
  }

  @Test
  @DisplayName("When descendant-or-self and there are no children should return self")
  void shouldReturnOnlySelfWhenThereAreNoChildren() {
    // given
    doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
    axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat(result).extracting("node").containsExactly(parentNode.getNode());
  }

  @Test
  @DisplayName("When descendant and there are no children should return empty")
  void shouldReturnEmptyWhenThereAreNoChildren() {
    // given
    doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
    axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should create element")
  void shouldCreateElement() {
    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, true);

        // then
        assertThat((Object) result).extracting("node", "position").containsExactly(node("name"), 1);
        verify(navigator).createElement(parentNode.getNode(), name);
        verify(navigator).appendChild(parentNode.getNode(), node("name"));
    }

  @Test
  @DisplayName("When wildcard namespace should throw")
  @SuppressWarnings("ReturnValueIgnored")
  void shouldThrowForElementsWithWildcardNamespace() {
    // given
    axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "elem"), false);

    // when
    assertThatThrownBy(
            () ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                    .collect(Collectors.toList()))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  @DisplayName("When wildcard local part should throw")
  @SuppressWarnings("ReturnValueIgnored")
  void shouldThrowForElementsWithWildcardLocalPart() {
    // given
    axisResolver =
        new DescendantOrSelfAxisResolver(new QName("http://www.example.com/my", "*", "my"), false);

    // when
    assertThatThrownBy(
            () ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                    .collect(Collectors.toList()))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testToString() {
    assertThat(axisResolver).hasToString("descendant-or-self::" + name);
  }

  @Override
  void setUpResolvableAxis() {
    doReturn(asList(node("node11"), node("node12")))
        .when(navigator)
        .elementsOf(parentNode.getNode());
    doReturn(asList(node("node1111"), node("node1112"))).when(navigator).elementsOf(node("node11"));
    doReturn(asList(node("node1211"), node("node1212"), node(name)))
        .when(navigator)
        .elementsOf(node("node12"));
  }
}
