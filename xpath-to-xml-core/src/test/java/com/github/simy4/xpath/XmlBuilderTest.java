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
package com.github.simy4.xpath;

import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;

class XmlBuilderTest {

  private final XmlBuilder xmlBuilder = new XmlBuilder();

  @Test
  @DisplayName("When no concrete SPI implementation should return false on can handle request")
  void shouldReturnFalseWithoutConcreteSpiImplementation() {
    // when
    assertThat(XmlBuilder.canHandle(new Object())).isTrue();
    assertThat(XmlBuilder.canHandle(null)).isFalse();
  }

  @Test
  @DisplayName("When no concrete SPI implementation should throw XmlBuilderException")
  void shouldThrowOnBuildWithoutConcreteSpiImplementation() {
    // given
    Object o = new Object();

    // when
    assertThat(xmlBuilder.build(o)).isSameAs(o);
    assertThatThrownBy(() -> xmlBuilder.build(null)).isInstanceOf(XmlBuilderException.class);
  }

  @Test
  @DisplayName("Should serialize it and deserialize it back")
  void shouldSerializeAndDeserializeBuilder()
      throws IOException, ClassNotFoundException, XPathExpressionException {
    // given
    XmlBuilder builder =
        new XmlBuilder(new SimpleNamespaceContext())
            .put("test")
            .putAll("test", "test", "test")
            .putAll(asList("test", "test", "test"))
            .put("test", 123)
            .putAll(singletonMap("test", 123))
            .remove("test")
            .removeAll("test", "test", "test")
            .removeAll(asList("test", "test", "test"));

    // when
    XmlBuilder deserializedBuild = SerializationHelper.serializeAndDeserializeBack(builder);

    // then
    assertThat(deserializedBuild).usingRecursiveComparison().isEqualTo(builder);
  }
}
