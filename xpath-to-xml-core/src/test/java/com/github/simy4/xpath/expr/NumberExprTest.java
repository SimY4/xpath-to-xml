package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NumberExprTest {

    @Mock
    private Navigator<String> navigator;

    private final Expr numberExpr = new NumberExpr(2.5);

    @Test
    public void shouldAlwaysReturnSingleNumberNode() {
        View<String> result = numberExpr.resolve(new ExprContext<String>(navigator, false, 1), node("xml"));
        assertThat(result).extracting("number").containsExactly(2.5);
    }

    @Test
    public void testToString() {
        assertThat(numberExpr).hasToString("2.5");
    }

}