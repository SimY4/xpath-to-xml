package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LiteralExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Test
    @DisplayName("Should always return single literal node")
    void shouldAlwaysReturnSingleLiteralNode() {
        View<TestNode> result = literalExpr.resolve(navigator, parentNode, false);
        assertThat(result).extracting("literal").contains("value");
    }

    @Test
    void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}