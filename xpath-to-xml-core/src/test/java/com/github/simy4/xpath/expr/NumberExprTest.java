package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NumberExprTest {

    @Mock
    private Navigator<String> navigator;

    private final Expr numberExpr = new NumberExpr(2.0);

    @Test
    public void shouldAlwaysReturnSingleNumberNode() {
        Set<NodeWrapper<String>> result = numberExpr.apply(new ExprContext<String>(navigator, 3, 1), node("xml"), false);
        assertThat(result).extracting("number", Number.class).containsExactly(2.0);
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("2.0");
    }

}