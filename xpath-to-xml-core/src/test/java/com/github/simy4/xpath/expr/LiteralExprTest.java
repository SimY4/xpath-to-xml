package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Mock private Navigator<TestNode> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Test
    public void shouldAlwaysReturnSingleLiteralNode() {
        View<TestNode> result = literalExpr.resolve(new ExprContext<TestNode>(navigator, false, parentNode));
        assertThat(result).extracting("literal").contains("value");
    }

    @Test
    public void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}