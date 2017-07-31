package com.github.simy4.xpath.expr.operators;

import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.utils.TestNode.node;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class MultiplicationTest {

    @DataPoints("3.0")
    public static final View[] NUMBERS = {
            new LiteralView<TestNode>("3.0"),
            new NumberView<TestNode>(3.0),
            new NodeView<TestNode>(node("3.0")),
            singleton(node("3.0")),
    };

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Navigator<TestNode> navigator;

    @Theory
    public void shouldMultiplyLeftViewToRightView(@FromDataPoints("3.0") View<TestNode> left,
                                                  @FromDataPoints("3.0") View<TestNode> right) {
        // given
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, 1);

        // when
        assertThat(Operator.multiplication.resolve(context, left, right)).extracting("number").contains(9.0);
    }

    @Test
    public void testToString() {
        assertThat(Operator.multiplication).hasToString("*");
    }

}