package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;
import org.junit.Before;
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
import static com.github.simy4.xpath.view.NodeSetView.empty;
import static com.github.simy4.xpath.view.NodeSetView.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class RootTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView<TestNode>("literal"),
            new NumberView<TestNode>(2.0),
            new NodeView<TestNode>(node("node")),
            BooleanView.of(true),
            BooleanView.of(false),
            empty(),
            singleton(node("node")),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<TestNode> navigator;

    private final StepExpr root = new Root();

    @Before
    public void setUp() {
        when(navigator.root()).thenReturn(node("root"));
    }

    @Theory
    public void shouldReturnSingleRootNodeOnTraverse(@FromDataPoints("parent nodes") View<TestNode> parentView) {
        // when
        NodeSetView<TestNode> result = root.resolve(new ExprContext<TestNode>(navigator, false, 1), parentView);

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("root"));
    }

    @Test
    public void testToString() {
        assertThat(root).hasToString("");
    }

}