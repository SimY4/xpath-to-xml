package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.Pair;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.utils.StringNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class UnaryExprTest {

    @DataPoints("views") public static Pair[] parentNodes = {
            Pair.of(new NumberView(2.0), new NumberView(-2.0)),
            Pair.of(new NumberView(-2.0), new NumberView(2.0)),
            Pair.of(new LiteralView("2.0"), new NumberView(-2.0)),
            Pair.of(new LiteralView("literal"), new NumberView(Double.NaN)),
            Pair.of(new NodeView<>(node("2.0")), new NumberView(-2.0)),
            Pair.of(new NodeView<>(node("node")), new NumberView(Double.NaN)),
            Pair.of(BooleanView.of(true), new NumberView<String>(-1.0)),
            Pair.of(BooleanView.of(false), new NumberView<String>(-0.0)),
            Pair.of(NodeSetView.empty(), new NumberView(Double.NaN)),
            Pair.of(NodeSetView.singleton(new NodeView<>(node("2.0"))), new NumberView(-2.0)),
            Pair.of(NodeSetView.singleton(new NodeView<>(node("node"))), new NumberView(Double.NaN)),
    };

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<String> navigator;
    @Mock private Expr valueExpr;

    private Expr unaryExpr;

    @Before
    public void setUp() {
        unaryExpr = new UnaryExpr(valueExpr);
    }

    @Theory
    public void shouldAlwaysReturnNegatedNumberViewNode(
            @FromDataPoints("views") Pair<View<String>, NumberView<String>> data) {
        when(valueExpr.resolve(ArgumentMatchers.<ExprContext<String>>any(), any())).thenReturn(data.getFirst());

        View<String> result = unaryExpr.resolve(new ExprContext<>(navigator, false, 1), new NodeView<>(node("xml")));
        assertThat(result).isEqualTo(data.getSecond());
    }

    @Test
    public void testToString() {
        assertThat(unaryExpr).hasToString("-(" + valueExpr + ')');
    }

}