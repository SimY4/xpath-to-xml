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

@RunWith(MockitoJUnitRunner.class)
public class IdentityTest {

    @Mock private Navigator<String> navigator;

    private final StepExpr identity = new Identity();

    @Test
    public void shouldReturnTheSameNodesOnTraverse() {
        List<NodeWrapper<String>> nodes = asList(node("1"), node("2"), node("3"));
        List<NodeWrapper<String>> result = identity.traverse(navigator, nodes);
        assertThat(result).isSameAs(nodes);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        identity.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(identity).hasToString(".");
    }

}