package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExprTest {

    @Mock private Navigator<String> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Test
    public void shouldAlwaysReturnSingleLiteralNode() {
        List<NodeWrapper<String>> result = literalExpr.apply(navigator, node("xml"), false);
        assertThat(result).extracting("literal", String.class).containsExactly("value");
    }

    @Test
    public void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}