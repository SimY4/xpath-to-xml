package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExprTest {

    @Mock private Navigator<String> navigator;

    private final Expr literalExpr = new LiteralExpr("value");

    @Before
    public void setUp() {
        when(navigator.createLiteral(anyString())).thenAnswer(new Answer<NodeWrapper<String>>() {
            public NodeWrapper<String> answer(InvocationOnMock invocationOnMock) {
                return node((String) invocationOnMock.getArguments()[0]);
            }
        });
    }

    @Test
    public void shouldAlwaysReturnSingleLiteralNode() {
        List<NodeWrapper<String>> result = literalExpr.apply(navigator, node("xml"), false);
        assertThat(result).containsExactly(node("value"));
    }

    @Test
    public void testToString() {
        assertThat(literalExpr).hasToString("'value'");
    }

}