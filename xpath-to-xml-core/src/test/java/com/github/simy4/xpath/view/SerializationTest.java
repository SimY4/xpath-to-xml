/*
 * Copyright 2019-2021 Alex Simkin
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
package com.github.simy4.xpath.view;

import com.github.simy4.xpath.helpers.SerializationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.Serializable;
import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class SerializationTest {

  static Stream<Arguments> views() {
    return Stream.of(
        arguments(BooleanView.of(true)),
        arguments(new LiteralView<>("literal")),
        arguments(NodeSetView.empty()),
        arguments(NodeSetView.of(singletonList(node("node")), n -> true)),
        arguments(
            NodeSetView.of(singletonList(node("node")), n -> true)
                .flatMap(node -> NodeSetView.of(asList(node("node1"), node("node2")), nn -> true))),
        arguments(new NodeView<>(node("node"))),
        arguments(
            new NodeView<>(node("node"))
                .flatMap(node -> NodeSetView.of(asList(node("node1"), node("node2")), nn -> true))),
        arguments(new NumberView<>(3.0)));
  }

  @ParameterizedTest(name = "Given a view {0}")
  @DisplayName("Should serialize it and deserialize it back")
  @MethodSource("views")
  void shouldSerializeAndDeserializeView(View<?> view) throws IOException, ClassNotFoundException {
    // given
    assumeThat(view).isNotInstanceOf(Serializable.class);

    // when
    var deserializedView = SerializationHelper.serializeAndDeserializeBack((Serializable) view);

    // then
    assertThat(deserializedView)
        .usingRecursiveComparison()
        .ignoringFields("nodeSet", "filter", "nodeSetView", "fmap")
        .isEqualTo(view);
  }
}
