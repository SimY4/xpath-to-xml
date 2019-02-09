package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
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
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessThanOrEqualsExprTest {

    private static Stream<View<?>> lesser() {
        return Stream.of(
                new LiteralView<>("1.0"),
                new NumberView<>(1.0),
                new NodeView<>(node("1.0")),
                NodeSetView.of(singletonList(node("1.0")), node -> true),
                BooleanView.of(true)
        );
    }

    private static Stream<View<?>> greater() {
        return Stream.of(
                new LiteralView<>("2.0"),
                new NumberView<>(2.0),
                new NodeView<>(node("2.0")),
                NodeSetView.of(singletonList(node("2.0")), node -> true)
        );
    }

    private static Stream<Arguments> lessThan() {
        return lesser().flatMap(l -> greater().map(g -> arguments(l, g)));
    }

    private static Stream<Arguments> equals() {
        return lesser().flatMap(l1 -> lesser().map(l2 -> arguments(l1, l2)));
    }

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    private Expr lessThanOrEqualsExpr;

    @BeforeEach
    void setUp() {
        lessThanOrEqualsExpr = new LessThanOrEqualsExpr(leftExpr, rightExpr);
    }

    @ParameterizedTest(name = "Given views {0} less than {1}")
    @DisplayName("Should resolve to true")
    @MethodSource("lessThan")
    void shouldResolveToTrueWhenLeftIsLessThanRight(View<Node> less, View<Node> greater) {
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(less);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(greater);

        // when
        View<TestNode> result = lessThanOrEqualsExpr.resolve(navigator, parentNode, false);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @ParameterizedTest(name = "Given views {1} greater than {0}")
    @DisplayName("Should resolve to false")
    @MethodSource("lessThan")
    void shouldResolveToFalseWhenLeftIsGreaterThanRight(View<Node> less, View<Node> greater) {
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(greater);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(less);

        // when
        View<TestNode> result = lessThanOrEqualsExpr.resolve(navigator, parentNode, false);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @ParameterizedTest(name = "Given views {0} equal to {1}")
    @DisplayName("Should resolve to true")
    @MethodSource("equals")
    void shouldResolveToTrueWhenLeftIsEqualToRight(View<Node> left, View<Node> right) {
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(left);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(right);

        // when
        View<TestNode> result = lessThanOrEqualsExpr.resolve(navigator, parentNode, false);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @ParameterizedTest(name = "Given views {1} greater than {0} and greedy context")
    @DisplayName("Should match and resolve to true")
    @MethodSource("lessThan")
    void shouldApplyRightViewToLeftViewWhenShouldCreate(View<Node> less, View<Node> greater) {
        assumeThat(greater).isInstanceOf(IterableNodeView.class);
        assumeThat(((Iterable<?>) greater)).isNotEmpty();

        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(greater);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(less);

        // when
        View<TestNode> result = lessThanOrEqualsExpr.resolve(navigator, parentNode, true);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
        verify(navigator).setText(any(TestNode.class), eq(less.toString()));
    }

    @ParameterizedTest(name = "Given views {0} less than {1} and greedy context")
    @DisplayName("Should throw on resolve")
    @MethodSource("lessThan")
    void shouldThrowWhenShouldCreate(View<Node> less, View<Node> greater) {
        assumeThat(greater).isNotInstanceOf(IterableNodeView.class);
        // given
        when(leftExpr.resolve(any(), any(), anyBoolean())).thenReturn(greater);
        when(rightExpr.resolve(any(), any(), anyBoolean())).thenReturn(less);

        // then
        assertThatThrownBy(() -> lessThanOrEqualsExpr.resolve(navigator, parentNode, true))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(lessThanOrEqualsExpr).hasToString(leftExpr.toString() + "<=" + rightExpr.toString());
    }

}