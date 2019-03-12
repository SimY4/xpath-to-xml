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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiplicationExprTest extends AbstractOperationExprTest {

    static Stream<View<?>> three() {
        return Stream.of(
                new LiteralView<>("3.0"),
                new NumberView<>(3.0),
                new NodeView<>(node("3.0")),
                NodeSetView.of(singletonList(node("3.0")), node -> true)
        );
    }

    static Stream<Arguments> numberPairs() {
        return three().flatMap(n1 -> three().map(n2 -> arguments(n1, n2)));
    }

    static Stream<View<?>> nan() {
        return Stream.of(
                new LiteralView<TestNode>("literal"),
                new NumberView<TestNode>(Double.NaN),
                new NodeView<>(node("text")),
                empty()
        );
    }

    static Stream<Arguments> numberAndNan() {
        return three().flatMap(n -> nan().map(nan -> arguments(n, nan)));
    }

    @Mock private Navigator<TestNode> navigator;

    @BeforeEach
    void setUp() {
        operationExpr = new MultiplicationExpr(leftExpr, rightExpr);
    }

    @ParameterizedTest(name = "Given {0} and {1}")
    @DisplayName("Should multiply number representations")
    @MethodSource("numberPairs")
    void shouldAddLeftViewToRightView(View<Node> left, View<Node> right) {
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

        // when
        assertThat(operationExpr.resolve(navigator, new NodeView<>(node("node")), false))
                .extracting("number").contains(9.0);
    }

    @ParameterizedTest(name = "Given {0} and {1}")
    @DisplayName("Should resolve to NaN")
    @MethodSource("numberAndNan")
    void additionWithNanShouldBeNan(View<Node> number, View<Node> nan) {
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(number);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(nan);

        // when
        assertThat(operationExpr.resolve(navigator, new NodeView<>(node("node")), false))
                .extracting("number").contains(Double.NaN);
    }

}