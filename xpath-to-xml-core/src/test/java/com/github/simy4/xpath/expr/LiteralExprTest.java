package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
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

import static com.github.simy4.xpath.utils.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class LiteralExprTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView<TestNode>("literal"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("node")),
            BooleanView.of(true),
            BooleanView.of(false),
            NodeSetView.empty(),
            NodeSetView.singleton(new NodeView<TestNode>(node("node"))),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Theory
    public void shouldAlwaysReturnSingleLiteralNode(@FromDataPoints("parent nodes") View<TestNode> parentView) {
        View<TestNode> result = literalExpr.resolve(new ExprContext<TestNode>(navigator, false, 1), parentView);
        assertThat(result).extracting("literal").contains("value");
    }

    @Test
    public void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}