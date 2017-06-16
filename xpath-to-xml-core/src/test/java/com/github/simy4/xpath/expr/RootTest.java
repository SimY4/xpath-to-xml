package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.LiteralView;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import com.github.simy4.xpath.navigator.view.NumberView;
import com.github.simy4.xpath.navigator.view.View;
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

import static com.github.simy4.xpath.utils.StringNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(Theories.class)
public class RootTest {

    @DataPoints("parent nodes") public static View[] parentNodes = {
            new LiteralView("literal"),
            new NumberView(2.0),
            new NodeView<String>(node("node")),
            NodeSetView.empty(),
            NodeSetView.singleton(new NodeView<String>(node("node"))),
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock private Navigator<String> navigator;

    private final StepExpr root = new Root();

    @Before
    public void setUp() {
        when(navigator.root()).thenReturn(node("root"));
    }

    @Theory
    public void shouldReturnSingleRootNodeOnTraverse(@FromDataPoints("parent nodes") View<String> parentView) {
        // when
        NodeSetView<String> result = root.resolve(new ExprContext<String>(navigator, false, 1), parentView);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<String>(node("root")));
    }

    @Test
    public void testToString() {
        assertThat(root).hasToString("");
    }

}