package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.util.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class NotEqualsExprTest {

    @DataPoints("ne left")
    public static final View[] EQ_TEST = {
            new LiteralView<TestNode>("2.0"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("2.0")),
    };

    @DataPoints("ne right")
    public static final View[] NE_TEST = {
            new LiteralView<TestNode>("literal"),
            new NumberView<TestNode>(10.0),
            new NodeView<TestNode>(node("node")),
            BooleanView.of(false),
            empty(),
    };

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;

    @Theory
    public void shouldAssociativelyResolveEqualViewsToFalse(@FromDataPoints("ne left") View<TestNode> left,
                                                            @FromDataPoints("ne left") View<TestNode> right) {
        // given
        when(leftExpr.resolve(any(ViewContext.class))).thenReturn(left);
        when(rightExpr.resolve(any(ViewContext.class))).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<TestNode>(navigator, parentNode, false);

        // when
        View<TestNode> leftToRight = new NotEqualsExpr(leftExpr, rightExpr).resolve(context);
        View<TestNode> rightToLeft = new NotEqualsExpr(rightExpr, leftExpr).resolve(context);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.of(false));
        assertThat(rightToLeft).isEqualTo(BooleanView.of(false));
    }

    @Theory
    public void shouldAssociativelyResolveNonEqualViewsToTrue(@FromDataPoints("ne left") View<TestNode> left,
                                                              @FromDataPoints("ne right") View<TestNode> right) {
        // given
        when(leftExpr.resolve(any(ViewContext.class))).thenReturn(left);
        when(rightExpr.resolve(any(ViewContext.class))).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<TestNode>(navigator, parentNode, false);

        // when
        View<TestNode> leftToRight = new NotEqualsExpr(leftExpr, rightExpr).resolve(context);
        View<TestNode> rightToLeft = new NotEqualsExpr(rightExpr, leftExpr).resolve(context);

        // then
        assertThat(leftToRight).isEqualTo(BooleanView.of(true));
        assertThat(rightToLeft).isEqualTo(BooleanView.of(true));
    }

    @Theory
    public void shouldApplyRightViewToLeftViewWhenShouldCreate(@FromDataPoints("ne left") View<TestNode> left,
                                                               @FromDataPoints("ne left") View<TestNode> right) {
        // given
        if (!(left instanceof IterableNodeView && ((IterableNodeView<TestNode>) left).iterator().hasNext())) {
            expectedException.expect(XmlBuilderException.class);
        }
        when(leftExpr.resolve(any(ViewContext.class))).thenReturn(left);
        when(rightExpr.resolve(any(ViewContext.class))).thenReturn(right);
        ViewContext<TestNode> context = new ViewContext<TestNode>(navigator, parentNode, true);

        // when
        View<TestNode> result = new NotEqualsExpr(leftExpr, rightExpr).resolve(context);

        // then
        assertThat(result).isEqualTo(BooleanView.of(true));
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(navigator).setText(any(TestNode.class), textCaptor.capture());
        assertThat(textCaptor.getValue()).isNotEqualTo(right.toString());
    }

    @Test
    public void testToString() {
        assertThat(new NotEqualsExpr(leftExpr, rightExpr))
                .hasToString(leftExpr.toString() + "!=" + rightExpr.toString());
    }

}