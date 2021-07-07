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
import static java.util.stream.StreamSupport.stream;

@MockitoSettings(strictness = Strictness.LENIENT)
class AttributeAxisResolverTest extends AbstractAxisResolverTest {

  @BeforeEach
  void setUp() {
    when(navigator.createAttribute(any(TestNode.class), eq(name)))
        .thenReturn(node(name.getLocalPart()));

    axisResolver = new AttributeAxisResolver(name);
  }

  @Test
  @DisplayName("Should create attribute")
  void shouldCreateAttribute() {
    // when
    IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, true);

    // then
    assertThat((Object) result).extracting("node", "position").containsExactly(node("name"), 1);
    verify(navigator).createAttribute(parentNode.getNode(), name);
  }

  @Test
  @DisplayName("When wildcard namespace should throw")
  @SuppressWarnings("ReturnValueIgnored")
  void shouldThrowForAttributesWithWildcardNamespace() {
    // given
    axisResolver = new AttributeAxisResolver(new QName("*", "attr"));

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
  void shouldThrowForAttributesWithWildcardLocalPart() {
    // given
    axisResolver = new AttributeAxisResolver(new QName("http://www.example.com/my", "*", "my"));

    // when
    assertThatThrownBy(
            () ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                    .collect(Collectors.toList()))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  @DisplayName("When error should propagate")
  @SuppressWarnings("ReturnValueIgnored")
  void shouldPropagateIfFailedToCreateAttribute() {
    // given
    when(navigator.createAttribute(any(TestNode.class), any(QName.class)))
        .thenThrow(XmlBuilderException.class);

    // when
    assertThatThrownBy(
            () ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                    .collect(Collectors.toList()))
        .isInstanceOf(XmlBuilderException.class);
  }

  @Test
  void testToString() {
    assertThat(axisResolver).hasToString("attribute::" + name);
  }

  @Override
  void setUpResolvableAxis() {
    doReturn(asList(node("name"), node("another-name")))
        .when(navigator)
        .attributesOf(parentNode.getNode());
  }
}
