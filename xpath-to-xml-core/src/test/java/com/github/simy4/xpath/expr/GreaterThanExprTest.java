package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
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
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class GreaterThanExprTest {

    @DataPoints("less")
    public static final View[] LESSER = {
            new LiteralView<>("1.0"),
            new NumberView<>(1.0),
            new NodeView<>(node("1.0")),
    };

    @DataPoints("greater")
    public static final View[] GREATER = {
            new LiteralView<>("2.0"),
            new NumberView<>(2.0),
            new NodeView<>(node("2.0")),
    };

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    @Theory
    public void shouldResolveToTrueWhenLeftIsGreaterThanRight(@FromDataPoints("less") View<Node> less,
                                                              @FromDataPoints("greater") View<Node> greater) {
        // given
        when(leftExpr.resolve(any())).thenReturn(greater);
        when(rightExpr.resolve(any())).thenReturn(less);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = new GreaterThanExpr(leftExpr, rightExpr).resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsLessThanRight(@FromDataPoints("less") View<Node> less,
                                                            @FromDataPoints("greater") View<Node> greater) {
        // given
        when(leftExpr.resolve(any())).thenReturn(less);
        when(rightExpr.resolve(any())).thenReturn(greater);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = new GreaterThanExpr(leftExpr, rightExpr).resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsEqualToRight(@FromDataPoints("less") View<Node> left,
                                                           @FromDataPoints("less") View<Node> right) {
        // given
        when(leftExpr.resolve(any())).thenReturn(left);
        when(rightExpr.resolve(any())).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, false);

        // when
        View<TestNode> result = new GreaterThanExpr(leftExpr, rightExpr).resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldThrowWhenResolveToFalseAndShouldCreate(@FromDataPoints("less") View<Node> less,
                                                             @FromDataPoints("greater") View<Node> greater) {
        // given
        expectedException.expect(XmlBuilderException.class);
        when(leftExpr.resolve(any())).thenReturn(less);
        when(rightExpr.resolve(any())).thenReturn(greater);
        ViewContext<TestNode> context = new ViewContext<>(navigator, parentNode, true);

        // when
        new GreaterThanExpr(leftExpr, rightExpr).resolve(context);
    }

    @Test
    public void testToString() {
        assertThat(new GreaterThanExpr(leftExpr, rightExpr))
                .hasToString(leftExpr.toString() + ">" + rightExpr.toString());
    }

}