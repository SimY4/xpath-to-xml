package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.Pair;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
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

import static com.github.simy4.xpath.util.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class UnaryExprTest {

    @DataPoints("views") public static Pair[] parentNodes = {
            Pair.of(new NumberView<TestNode>(2.0), new NumberView(-2.0)),
            Pair.of(new NumberView<TestNode>(-2.0), new NumberView(2.0)),
            Pair.of(new LiteralView<TestNode>("2.0"), new NumberView(-2.0)),
            Pair.of(new LiteralView<TestNode>("literal"), new NumberView(Double.NaN)),
            Pair.of(new NodeView<TestNode>(node("2.0")), new NumberView(-2.0)),
            Pair.of(new NodeView<TestNode>(node("node")), new NumberView(Double.NaN)),
            Pair.of(BooleanView.of(true), new NumberView<TestNode>(-1.0)),
            Pair.of(BooleanView.of(false), new NumberView<TestNode>(-0.0)),
            Pair.of(empty(), new NumberView(Double.NaN)),
    };

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr valueExpr;

    private Expr unaryExpr;

    @Before
    public void setUp() {
        unaryExpr = new UnaryExpr(valueExpr);
    }

    @Theory
    public void shouldAlwaysReturnNegatedNumberViewNode(
            @FromDataPoints("views") Pair<View<TestNode>, NumberView<TestNode>> data) {
        when(valueExpr.resolve(ArgumentMatchers.<ViewContext<TestNode>>any())).thenReturn(data.getFirst());

        View<TestNode> result = unaryExpr.resolve(new ViewContext<TestNode>(navigator,
                new NodeView<TestNode>(node("xml")), false));
        assertThat(result.toNumber()).isEqualTo(data.getSecond().toNumber());
    }

    @Test
    public void testToString() {
        assertThat(unaryExpr).hasToString("-(" + valueExpr + ')');
    }

}