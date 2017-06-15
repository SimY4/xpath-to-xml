package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
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
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class EqualsTest {

    @DataPoints("eq left")
    public static final View[] EQ_TEST = {
            new LiteralView("2.0"),
            new NumberView(2.0),
            new NodeView<>(node("2.0")),
            singleton(new LiteralView<>("2.0")),
            singleton(new NumberView<>(2.0)),
            singleton(new NodeView<>(node("2.0"))),
    };

    @DataPoints("eq right")
    public static final View[] NE_TEST = {
            new LiteralView("literal"),
            new NumberView(10.0),
            new NodeView<>(node("node")),
            BooleanView.falsy(),
            empty(),
            singleton(new LiteralView<>("literal")),
            singleton(new NumberView<>(10.0)),
            singleton(new NodeView<>(node("node"))),
            singleton(BooleanView.falsy()),
    };

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<String> navigator;

    @Theory
    public void shouldAssociativelyResolveEqualViewsToTrue(@FromDataPoints("eq left") View<String> left,
                                                           @FromDataPoints("eq left") View<String> right) {
        // given
        ExprContext<String> context = new ExprContext<>(navigator, false, 1);

        // when
        View<String> leftToRight = Operator.equals.resolve(context, left, right);
        View<String> rightToLeft = Operator.equals.resolve(context, right, left);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.truthy());
        assertThat(rightToLeft).isEqualTo(BooleanView.truthy());
    }

    @Theory
    public void shouldAssociativelyResolveNonEqualViewsToFalse(@FromDataPoints("eq left") View<String> left,
                                                               @FromDataPoints("eq right") View<String> right) {
        // given
        ExprContext<String> context = new ExprContext<>(navigator, false, 1);

        // when
        View<String> leftToRight = Operator.equals.resolve(context, left, right);
        View<String> rightToLeft = Operator.equals.resolve(context, right, left);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.falsy());
        assertThat(rightToLeft).isEqualTo(BooleanView.falsy());
    }

    @Theory
    public void shouldApplyRightViewToLeftViewWhenShouldCreate(@FromDataPoints("eq left") View<String> left,
                                                               @FromDataPoints("eq right") View<String> right) {
        // given
        if (!(left instanceof NodeView)
                && (!(left instanceof NodeSetView) || !(((NodeSetView) left).iterator().next() instanceof NodeView))) {
            expectedException.expect(XmlBuilderException.class);
        }
        ExprContext<String> context = new ExprContext<>(navigator, true, 1);
        context.advance();

        // when
        View<String> result = Operator.equals.resolve(context, left, right);

        // then
        assertThat(result).isEqualTo(BooleanView.truthy());
        verify(navigator).setText(any(), eq(right.toString()));
    }

    @Test
    public void testToString() {
        assertThat(Operator.equals).hasToString("=");
    }

}