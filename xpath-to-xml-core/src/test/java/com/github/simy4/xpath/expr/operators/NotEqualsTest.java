package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
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

import static com.github.simy4.xpath.utils.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class NotEqualsTest {

    @DataPoints("ne left")
    public static final View[] EQ_TEST = {
            new LiteralView<TestNode>("2.0"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("2.0")),
            singleton(new LiteralView<TestNode>("2.0")),
            singleton(new NumberView<TestNode>(2.0)),
            singleton(new NodeView<TestNode>(node("2.0"))),
    };

    @DataPoints("ne right")
    public static final View[] NE_TEST = {
            new LiteralView<TestNode>("literal"),
            new NumberView<TestNode>(10.0),
            new NodeView<TestNode>(node("node")),
            BooleanView.of(false),
            empty(),
            singleton(new LiteralView<TestNode>("literal")),
            singleton(new NumberView<TestNode>(10.0)),
            singleton(new NodeView<TestNode>(node("node"))),
            singleton(BooleanView.of(false)),
    };

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;

    @Theory
    public void shouldAssociativelyResolveEqualViewsToFalse(@FromDataPoints("ne left") View<TestNode> left,
                                                            @FromDataPoints("ne left") View<TestNode> right) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        View<TestNode> leftToRight = Operator.notEquals.resolve(context, left, right);
        View<TestNode> rightToLeft = Operator.notEquals.resolve(context, right, left);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.of(false));
        assertThat(rightToLeft).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldAssociativelyResolveNonEqualViewsToTrue(@FromDataPoints("ne left") View<TestNode> left,
                                                              @FromDataPoints("ne right") View<TestNode> right) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        View<TestNode> leftToRight = Operator.notEquals.resolve(context, left, right);
        View<TestNode> rightToLeft = Operator.notEquals.resolve(context, right, left);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.of(true));
        assertThat(rightToLeft).isEqualTo(BooleanView.of(true));
    }

    @Theory
    public void shouldApplyRightViewToLeftViewWhenShouldCreate(@FromDataPoints("ne left") View<TestNode> left,
                                                               @FromDataPoints("ne left") View<TestNode> right) {
        // given
        if (!(left instanceof NodeView)
                && (!(left instanceof NodeSetView) || !(((NodeSetView) left).iterator().next() instanceof NodeView))) {
            expectedException.expect(XmlBuilderException.class);
        }
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, true, 1);
        context.advance();

        // when
        View<TestNode> result = Operator.notEquals.resolve(context, left, right);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
        verify(navigator).setText(any(TestNode.class), eq(Boolean.toString(!right.toBoolean())));
    }

    @Test
    public void testToString() {
        assertThat(Operator.notEquals).hasToString("!=");
    }

}