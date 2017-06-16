package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;
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

@RunWith(Theories.class)
public class NumberExprTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView("literal"),
            new NumberView(2.0),
            new NodeView<String>(node("node")),
            NodeSetView.empty(),
            NodeSetView.singleton(new NodeView<String>(node("node"))),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Navigator<String> navigator;

    private final Expr numberExpr = new NumberExpr(2.5);

    @Theory
    public void shouldAlwaysReturnSingleNumberNode(@FromDataPoints("parent nodes") View<String> parentView) {
        View<String> result = numberExpr.resolve(new ExprContext<String>(navigator, false, 1), parentView);
        assertThat(result).extracting("number").containsExactly(2.5);
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("2.5");
    }

}