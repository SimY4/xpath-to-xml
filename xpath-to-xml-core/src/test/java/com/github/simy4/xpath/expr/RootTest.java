package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RootTest {

    @Mock private Navigator<String> navigator;

    private final StepExpr root = new Root();

    @Before
    public void setUp() {
        when(navigator.root()).thenReturn(node("root"));
    }

    @Test
    public void shouldReturnSingleRootNodeOnTraverse() {
        // when
        Set<NodeView<String>> result = root.resolve(new ExprContext<String>(navigator, false, 1), node("node"));

        // then
        assertThat(result).containsExactly(node("root"));
    }

    @Test
    public void testToString() {
        assertThat(root).hasToString("");
    }

}