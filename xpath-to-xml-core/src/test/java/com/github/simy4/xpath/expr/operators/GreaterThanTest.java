package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
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

import static com.github.simy4.xpath.utils.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class GreaterThanTest {

    @DataPoints("less")
    public static final View[] LESSER = {
            new LiteralView<TestNode>("1.0"),
            new NumberView<TestNode>(1.0),
            new NodeView<TestNode>(node("1.0")),
            singleton(new NodeView<TestNode>(node("1.0"))),
    };

    @DataPoints("greater")
    public static final View[] GREATER = {
            new LiteralView<TestNode>("2.0"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("2.0")),
            singleton(new NodeView<TestNode>(node("2.0"))),
    };

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;

    @Theory
    public void shouldResolveToTrueWhenLeftIsGreaterThanRight(@FromDataPoints("less") View<TestNode> less,
                                                              @FromDataPoints("greater") View<TestNode> greater) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        View<TestNode> result = Operator.greaterThan.resolve(context, greater, less);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsLessThanRight(@FromDataPoints("less") View<TestNode> less,
                                                            @FromDataPoints("greater") View<TestNode> greater) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        View<TestNode> result = Operator.greaterThan.resolve(context, less, greater);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsEqualToRight(@FromDataPoints("less") View<TestNode> left,
                                                           @FromDataPoints("less") View<TestNode> right) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        View<TestNode> result = Operator.greaterThan.resolve(context, left, right);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldThrowWhenResolveToFalseAndShouldCreate(@FromDataPoints("less") View<TestNode> less,
                                                             @FromDataPoints("greater") View<TestNode> greater) {
        // given
        expectedException.expect(XmlBuilderException.class);
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, true, 1);
        context.advance();

        // when
        Operator.greaterThan.resolve(context, less, greater);
    }

    @Test
    public void testToString() {
        assertThat(Operator.greaterThan).hasToString(">");
    }

}