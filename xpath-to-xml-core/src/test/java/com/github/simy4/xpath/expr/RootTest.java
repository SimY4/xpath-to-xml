package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
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
        List<NodeWrapper<String>> nodes = asList(node("1"), node("2"), node("3"));
        List<NodeWrapper<String>> result = root.traverse(navigator, nodes);
        assertThat(result).containsExactly(node("root"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        root.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(root).hasToString("/");
    }

}