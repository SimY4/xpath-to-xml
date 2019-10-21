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
class NumberExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock
    private Navigator<TestNode> navigator;

    private final Expr numberExpr = new NumberExpr(3.0);

    @Test
    @DisplayName("Should always return single number node")
    void shouldAlwaysReturnSingleNumberNode() {
        View<TestNode> result = numberExpr.resolve(navigator, parentNode, false);
        assertThat(result).extracting("number").isEqualTo(3.0);
    }

    @Test
    void testToString() {
        assertThat(numberExpr).hasToString("3.0");
    }

}