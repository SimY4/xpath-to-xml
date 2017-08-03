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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NumberExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Mock
    private Navigator<TestNode> navigator;

    private final Expr numberExpr = new NumberExpr(3.0);

    @Test
    public void shouldAlwaysReturnSingleNumberNode() {
        View<TestNode> result = numberExpr.resolve(new ExprContext<TestNode>(navigator, false, parentNode));
        assertThat(result).extracting("number").contains(3.0);
    }

    @Test
    public void shouldPrependMissingNodesAndReturnNumberOnGreedyMatching() {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, true,
                new NodeView<TestNode>(node("node")));
        context.next();

        // when
        boolean result = numberExpr.match(context);

        // then
        assertThat(result).isEqualTo(true);
        verify(navigator, times(2)).prependCopy(node("node"));
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("3.0");
    }

}