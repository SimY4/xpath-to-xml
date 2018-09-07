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
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GreaterThanOrEqualsExprTest {

    private static Stream<View<?>> lesser() {
        return Stream.of(
                new LiteralView<>("1.0"),
                new NumberView<>(1.0),
                new NodeView<>(node("1.0")),
                new NodeSetView<>(() -> singletonList(new NodeView<>(node("1.0"))).iterator()),
                BooleanView.of(true)
        );
    }

    private static Stream<View<?>> greater() {
        return Stream.of(
                new LiteralView<>("2.0"),
                new NumberView<>(2.0),
                new NodeView<>(node("2.0")),
                new NodeSetView<>(() -> singletonList(new NodeView<>(node("2.0"))).iterator())
        );
    }

    private static Stream<Arguments> lessThan() {
        return lesser().flatMap(l -> greater().map(g -> Arguments.of(l, g)));
    }

    private static Stream<Arguments> equals() {
        return lesser().flatMap(l1 -> lesser().map(l2 -> Arguments.of(l1, l2)));
    }

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    private Expr greaterThanOrEqualsExpr;

    @BeforeEach
    void setUp() {
        greaterThanOrEqualsExpr = new GreaterThanOrEqualsExpr(leftExpr, rightExpr);
    }

    @ParameterizedTest(name = "Given views {1} greater than {0}")
    @DisplayName("Should resolve to true")
    @MethodSource("lessThan")
    void shouldResolveToTrueWhenLeftIsGreaterThanRight(View<Node> less, View<Node> greater) {
        // given
        when(leftExpr.resolve(any())).thenReturn(greater);
        when(rightExpr.resolve(any())).thenReturn(less);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = greaterThanOrEqualsExpr.resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @ParameterizedTest(name = "Given views {0} less than {1}")
    @DisplayName("Should resolve to false")
    @MethodSource("lessThan")
    void shouldResolveToFalseWhenLeftIsLessThanRight(View<Node> less, View<Node> greater) {
        // given
        when(leftExpr.resolve(any())).thenReturn(less);
        when(rightExpr.resolve(any())).thenReturn(greater);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = greaterThanOrEqualsExpr.resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @ParameterizedTest(name = "Given views {0} equal to {1}")
    @DisplayName("Should resolve to true")
    @MethodSource("equals")
    void shouldResolveToTrueWhenLeftIsEqualToRight(View<Node> left, View<Node> right) {
        // given
        when(leftExpr.resolve(any())).thenReturn(left);
        when(rightExpr.resolve(any())).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = greaterThanOrEqualsExpr.resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @ParameterizedTest(name = "Given views {0} less than {1} and greedy context")
    @DisplayName("Should match and resolve to true")
    @MethodSource("lessThan")
    void shouldApplyRightViewToLeftViewWhenShouldCreate(View<Node> less, View<Node> greater) {
        assumeThat(less).isInstanceOf(IterableNodeView.class);
        assumeThat(((Iterable<?>) less)).isNotEmpty();

        // given
        when(leftExpr.resolve(any())).thenReturn(less);
        when(rightExpr.resolve(any())).thenReturn(greater);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, true);

        // when
        View<TestNode> result = greaterThanOrEqualsExpr.resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
        verify(navigator).setText(any(TestNode.class), eq(greater.toString()));
    }

    @ParameterizedTest(name = "Given views {0} less than {1} and greedy context")
    @DisplayName("Should throw on resolve")
    @MethodSource("lessThan")
    void shouldThrowWhenShouldCreate(View<Node> less, View<Node> greater) {
        assumeThat(less).isNotInstanceOf(IterableNodeView.class);
        // given
        when(leftExpr.resolve(any())).thenReturn(less);
        when(rightExpr.resolve(any())).thenReturn(greater);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, true);

        // then
        assertThatThrownBy(() -> greaterThanOrEqualsExpr.resolve(context))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(new GreaterThanOrEqualsExpr(leftExpr, rightExpr))
                .hasToString(leftExpr.toString() + ">=" + rightExpr.toString());
    }

}