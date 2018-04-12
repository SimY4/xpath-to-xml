package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class AdditionExprTest {

    @DataPoints("3.0")
    public static final View[] NUMBERS = {
            new LiteralView<>("3.0"),
            new NumberView<>(3.0),
            new NodeView<>(node("3.0")),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    @Theory
    public void shouldAddLeftViewToRightView(@FromDataPoints("3.0") View<Node> left,
                                             @FromDataPoints("3.0") View<Node> right) {
        // given
        when(leftExpr.resolve(any())).thenReturn(left);
        when(rightExpr.resolve(any())).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        assertThat(new AdditionExpr(leftExpr, rightExpr).resolve(context)).extracting("number").contains(6.0);
    }

    @Test
    public void testToString() {
        assertThat(new AdditionExpr(leftExpr, rightExpr)).hasToString(leftExpr.toString() + "+" + rightExpr.toString());
    }

}