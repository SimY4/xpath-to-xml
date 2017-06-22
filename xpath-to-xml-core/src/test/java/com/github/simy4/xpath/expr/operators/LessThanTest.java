package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
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

import static com.github.simy4.xpath.utils.StringNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class LessThanTest {

    @DataPoints("less")
    public static final View[] LESSER = {
            new LiteralView("1.0"),
            new NumberView(1.0),
            new NodeView<String>(node("1.0")),
            singleton(new LiteralView("1.0")),
            singleton(new NumberView(1.0)),
            singleton(new NodeView<String>(node("1.0"))),
    };

    @DataPoints("greater")
    public static final View[] GREATER = {
            new LiteralView("2.0"),
            new NumberView(2.0),
            new NodeView<String>(node("2.0")),
            singleton(new LiteralView("2.0")),
            singleton(new NumberView(2.0)),
            singleton(new NodeView<String>(node("2.0"))),
    };

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<String> navigator;

    @Theory
    public void shouldResolveToTrueWhenLeftIsLessThanRight(@FromDataPoints("less") View<String> less,
                                                           @FromDataPoints("greater") View<String> greater) {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 1);

        // when
        View<String> result = Operator.lessThan.resolve(context, less, greater);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsGreaterThanRight(@FromDataPoints("less") View<String> less,
                                                               @FromDataPoints("greater") View<String> greater) {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 1);

        // when
        View<String> result = Operator.lessThan.resolve(context, greater, less);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldResolveToFalseWhenLeftIsEqualToRight(@FromDataPoints("less") View<String> left,
                                                           @FromDataPoints("less") View<String> right) {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 1);

        // when
        View<String> result = Operator.lessThan.resolve(context, left, right);

        // then
        assertThat(result).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldThrowWhenResolveToFalseAndShouldCreate(@FromDataPoints("less") View<String> less,
                                                             @FromDataPoints("greater") View<String> greater) {
        // given
        expectedException.expect(XmlBuilderException.class);
        ExprContext<String> context = new ExprContext<String>(navigator, true, 1);
        context.advance();

        // when
        Operator.lessThan.resolve(context, greater, less);
    }

    @Test
    public void testToString() {
        assertThat(Operator.lessThan).hasToString("<");
    }

}