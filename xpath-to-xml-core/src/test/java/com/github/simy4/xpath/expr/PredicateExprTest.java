package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.axis.SelfAxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.stream.Stream;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PredicateExprTest {

    private static Stream<Arguments> truthy() {
        return Stream.of(
                arguments(new LiteralExpr("2.0")),
                arguments(new EqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0))),
                arguments(new AxisStepExpr(new SelfAxisResolver(new QName("*", "*")), Collections.emptySet()))
        );
    }

    private static Stream<Arguments> falsy() {
        return Stream.of(
                arguments(new LiteralExpr("")),
                arguments(new NotEqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0)))
        );
    }

    @Mock private Navigator<TestNode> navigator;

    @ParameterizedTest(name = "Given truthy predicate {0}")
    @DisplayName("Should resolve to true")
    @MethodSource("truthy")
    void shouldReturnTrueForTruthyPredicate(Expr truthy) {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        boolean result = new PredicateExpr(truthy).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(true);
    }

    @ParameterizedTest(name = "Given falsy predicate {0}")
    @DisplayName("Should resolve to false")
    @MethodSource("falsy")
    void shouldReturnFalseForNonGreedyFalsePredicate(Expr falsy) {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        boolean result = new PredicateExpr(falsy).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("When greedy context, falsy predicate and non new node should return false")
    void shouldReturnFalseOnGreedyFalseResolveAndNonNewNode() {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), true);

        // when
        boolean result = new PredicateExpr(new NumberExpr(3.0)).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("When greedy context, falsy predicate and new node should prepend missing nodes and return true")
    void shouldPrependMissingNodesAndReturnTrueOnGreedyFalsePredicateAndNewNode() {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node"), true), true);

        // when
        boolean result = new PredicateExpr(new NumberExpr(3.0)).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(true);
        verify(navigator, times(2)).prependCopy(node("node"));
    }

    @Test
    void testToString() {
        // given
        Expr predicate = mock(Expr.class);

        // then
        assertThat(new PredicateExpr(predicate)).hasToString("[" + predicate + ']');
    }

}
