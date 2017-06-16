package com.github.simy4.xpath.expr.op;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.utils.StringNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class EqTest {

    @DataPoints("eq left")
    public static final View[] EQ_TEST = {
            new LiteralView("2.0"),
            new NumberView(2.0),
            new NodeView<String>(node("2.0")),
            singleton(new LiteralView("2.0")),
            singleton(new NumberView(2.0)),
            singleton(new NodeView<String>(node("2.0"))),
    };

    @DataPoints("eq right")
    public static final View[] NE_TEST = {
            new LiteralView("literal"),
            new NumberView(10.0),
            new NodeView<String>(node("node")),
            singleton(new LiteralView("literal")),
            singleton(new NumberView(10.0)),
            singleton(new NodeView<String>(node("node"))),
    };

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<String> navigator;

    @Theory
    public void shouldMatchLeftEqualViewToRightEqualViewOnTest(@FromDataPoints("eq left") View<String> left,
                                                               @FromDataPoints("eq left") View<String> right) {
        // when
        boolean result = new Eq().test(left, right);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Theory
    public void shouldMatchRightEqualViewToLeftEqualViewOnTest(@FromDataPoints("eq left") View<String> left,
                                                               @FromDataPoints("eq left") View<String> right) {
        // when
        boolean result = new Eq().test(right, left);

        // then
        assertThat(result).isEqualTo(true);
    }

    @Theory
    public void shouldMismatchLeftNonEqualViewToRightNonEqualViewOnTest(
            @FromDataPoints("eq left") View<String> left,
            @FromDataPoints("eq right") View<String> right) {
        // when
        boolean result = new Eq().test(left, right);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Theory
    public void shouldMismatchRightNonEqualViewToLeftNonEqualViewOnTest(
            @FromDataPoints("eq left") View<String> left,
            @FromDataPoints("eq right") View<String> right) {
        // when
        boolean result = new Eq().test(right, left);

        // then
        assertThat(result).isEqualTo(false);
    }

    @Theory
    public void shouldApplyRightTextContentToLeftView(@FromDataPoints("eq left") View<String> left,
                                                      @FromDataPoints("eq right") View<String> right) {
        // given
        assumeTrue(left instanceof NodeView
                || (left instanceof NodeSetView && ((NodeSetView) left).iterator().next() instanceof NodeView));

        // when
        new Eq().apply(navigator, left, right);

        // then
        verify(navigator).setText(ArgumentMatchers.<Node<String>>any(), eq(right.toString()));
    }

    @Theory
    public void shouldFailToApplyOp(@FromDataPoints("eq left") View<String> left) {
        // given
        assumeFalse(left instanceof NodeView
                || (left instanceof NodeSetView && ((NodeSetView) left).iterator().next() instanceof NodeView));

        // given
        expectedException.expect(XmlBuilderException.class);

        // when
        new Eq().apply(navigator, left, left);
    }

    @Test
    public void testToString() {
        assertThat(new Eq()).hasToString("=");
    }

}