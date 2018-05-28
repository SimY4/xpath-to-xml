package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NumberExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock
    private Navigator<TestNode> navigator;

    private final Expr numberExpr = new NumberExpr(3.0);

    @Test
    public void shouldAlwaysReturnSingleNumberNode() {
        View<TestNode> result = numberExpr.resolve(new ViewContext<>(navigator, parentNode, false));
        assertThat(result).extracting("number").contains(3.0);
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("3.0");
    }

}