package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.utils.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class NumberExprTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView<TestNode>("literal"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("node")),
            BooleanView.of(true),
            BooleanView.of(false),
            empty(),
            singleton(new NodeView<TestNode>(node("node"))),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Navigator<TestNode> navigator;

    private final Expr numberExpr = new NumberExpr(3.0);

    @Theory
    public void shouldAlwaysReturnSingleNumberNode(@FromDataPoints("parent nodes") View<TestNode> parentView) {
        View<TestNode> result = numberExpr.resolve(new ExprContext<TestNode>(navigator, false, 1), parentView);
        assertThat(result).extracting("number").contains(3.0);
    }

    @Test
    public void shouldPrependMissingNodesAndReturnNumberOnGreedyMatching() {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, true, 1);
        context.advance();

        // when
        boolean result = numberExpr.match(context, new NodeView<TestNode>(node("node")));

        // then
        assertThat(result).isEqualTo(true);
        verify(navigator, times(2)).prependCopy(node("node"));
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("3.0");
    }

}