package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
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
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnaryExprTest {

    static Stream<Arguments> number() {
        return Stream.of(
                arguments(new LiteralView<>("2.0")),
                arguments(new NumberView<>(2.0)),
                arguments(new NodeView<>(node("2.0"))),
                arguments(new NodeSetView<>(() -> singletonList(new NodeView<>(node("2.0"))).iterator()))
        );
    }

    static Stream<Arguments> nan() {
        return Stream.of(
                arguments(new LiteralView<>("literal")),
                arguments(new NumberView<>(Double.NaN)),
                arguments(new NodeView<>(node("text"))),
                arguments(empty())
        );
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
        when(valueExpr.resolve(any())).thenReturn(number);
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        assertThat(unaryExpr.resolve(context)).extracting("number").contains(-number.toNumber());
    }

    @ParameterizedTest(name = "Given {0}")
    @DisplayName("Should resolve to NaN")
    @MethodSource("nan")
    void negationWithNanShouldBeNan(View<Node> nan) {
        // given
        when(valueExpr.resolve(any())).thenReturn(nan);
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        assertThat(unaryExpr.resolve(context)).extracting("number").contains(Double.NaN);
    }

    @Test
    void testToString() {
        assertThat(unaryExpr).hasToString("-(" + valueExpr + ')');
    }

}