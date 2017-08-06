package com.github.simy4.xpath.expr.operators;

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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class AdditionTest {

    @DataPoints("3.0")
    public static final View[] NUMBERS = {
            new LiteralView<>("3.0"),
            new NumberView<>(3.0),
            new NodeView<>(node("3.0")),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;

    @Theory
    public void shouldAddLeftViewToRightView(@FromDataPoints("3.0") View<TestNode> left,
                                             @FromDataPoints("3.0") View<TestNode> right) {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        assertThat(Operator.addition.resolve(context, left, right)).extracting("number").contains(6.0);
    }

    @Test
    public void testToString() {
        assertThat(Operator.addition).hasToString("+");
    }

}