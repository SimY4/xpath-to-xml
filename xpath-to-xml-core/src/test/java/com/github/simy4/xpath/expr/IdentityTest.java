package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdentityTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;

    private StepExpr identity;

    @Before
    public void setUp() {
        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });

        identity = new Identity(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnTheSameNodesOnTraverse() {
        NodeWrapper<String> node1 = node("1");
        NodeWrapper<String> node2 = node("2");
        NodeWrapper<String> node3 = node("3");
        List<NodeWrapper<String>> result = identity.traverse(navigator, asList(node1, node2, node3));
        assertThat(result).containsExactly(node1, node2, node3);
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), eq(false)))
                .thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = identity.traverse(navigator, asList(node("1"), node("2"), node("3")));
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        identity.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(identity).hasToString(".[" + predicate1 + "][" + predicate2 + ']');
    }

}