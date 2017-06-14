package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExprTest {

    @Mock private Navigator<String> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Test
    public void shouldAlwaysReturnSingleLiteralNode() {
        Set<NodeView<String>> result = literalExpr
                .resolve(new ExprContext<String>(navigator, false, 1), node("xml"));
        assertThat(result).extracting("literal", String.class).containsExactly("value");
    }

    @Test
    public void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}