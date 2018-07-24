package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class MultiplicationExprTest {

    @DataPoints("3.0")
    public static final View<?>[] NUMBERS = {
            new LiteralView<TestNode>("3.0"),
            new NumberView<TestNode>(3.0),
            new NodeView<TestNode>(node("3.0")),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    @Theory
    public void shouldMultiplyLeftViewToRightView(@FromDataPoints("3.0") View<TestNode> left,
                                                  @FromDataPoints("3.0") View<TestNode> right) {
        // given
        when(leftExpr.resolve(ArgumentMatchers.<ViewContext<TestNode>>any())).thenReturn(left);
        when(rightExpr.resolve(ArgumentMatchers.<ViewContext<TestNode>>any())).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<TestNode>(navigator,
                new NodeView<TestNode>(node("node")), false);

        // when
        assertThat(new MultiplicationExpr(leftExpr, rightExpr).resolve(context)).extracting("number").contains(9.0);
    }

    @Test
    public void testToString() {
        assertThat(new MultiplicationExpr(leftExpr, rightExpr))
                .hasToString(leftExpr.toString() + "*" + rightExpr.toString());
    }

}