package com.github.simy4.xpath.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SerializationTest {

    private static Stream<Arguments> views() {
        return Stream.of(
                arguments(BooleanView.of(true)),
                arguments(new LiteralView<>("literal")),
                arguments(NodeSetView.of(singletonList(node("3.0")), node -> true)),
                arguments(NodeSetView.of(singletonList(node("node")), n -> true)
                        .flatMap(node -> NodeSetView.of(asList(node("node1"), node("node2")), nn -> true))),
                arguments(new NodeView<>(node("node"))),
                arguments(new NodeView<>(node("node"))
                        .flatMap(node -> NodeSetView.of(asList(node("node1"), node("node2")), nn -> true))),
                arguments(new NumberView<>(3.0)));
    }

    @ParameterizedTest(name = "Given a view {0}")
    @DisplayName("Should serialize it and deserialize it back")
    @MethodSource("views")
    void shouldSerializeAndDeserializeView(View<?> view) throws IOException, ClassNotFoundException {
        // when
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(view);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        View<?> deserializedView = (View<?>) new ObjectInputStream(in).readObject();

        assertThat(deserializedView).isEqualToIgnoringGivenFields(view, "nodeSet", "filter", "nodeSetView", "fmap");
    }

}
