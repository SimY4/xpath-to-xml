package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
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
public class ParentTest {

    @Mock private Navigator<String> navigator;

    private final StepExpr parent = new Parent();

    @Test
    public void shouldReturnSingleRootNodeOnTraverse() {
        when(navigator.parentOf(node("1"))).thenReturn(node("parent1"));
        when(navigator.parentOf(node("2"))).thenReturn(null);
        when(navigator.parentOf(node("3"))).thenReturn(node("parent2"));
        when(navigator.parentOf(node("4"))).thenReturn(node("parent1"));
        List<NodeWrapper<String>> result = parent.traverse(navigator,
                asList(node("1"), node("2"), node("3"), node("4")));
        assertThat(result).containsExactly(node("parent1"), node("parent2"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        parent.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(parent).hasToString("..");
    }

}