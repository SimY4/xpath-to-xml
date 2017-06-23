package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
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

import static com.github.simy4.xpath.utils.StringNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class NumberExprTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView("literal"),
            new NumberView(2.0),
            new NodeView<String>(node("node")),
            BooleanView.of(true),
            BooleanView.of(false),
            NodeSetView.empty(),
            NodeSetView.singleton(new NodeView<String>(node("node"))),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Navigator<String> navigator;

    private final Expr numberExpr = new NumberExpr(3.0);

    @Theory
    public void shouldAlwaysReturnSingleNumberNode(@FromDataPoints("parent nodes") View<String> parentView) {
        View<String> result = numberExpr.resolve(new ExprContext<String>(navigator, false, 1), parentView);
        assertThat(result).isEqualTo(new NumberView<String>(3.0));
    }

    @Test
    public void shouldPrependMissingNodesAndReturnNumberOnGreedyMatching() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, true, 1);
        context.advance();

        // when
        boolean result = numberExpr.match(context, new NodeView<String>(node("node")));

        // then
        assertThat(result).isEqualTo(true);
        verify(navigator, times(2)).prependCopy(node("node"));
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("3.0");
    }

}