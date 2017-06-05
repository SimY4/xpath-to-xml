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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParentTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;

    private StepExpr parent;

    @Before
    public void setUp() {
        when(navigator.parentOf(ArgumentMatchers.<NodeWrapper<String>>any()))
                .thenAnswer(new Answer<NodeWrapper<String>>() {
                    @Override
                    public NodeWrapper<String> answer(InvocationOnMock invocationOnMock) {
                        String[] nodeNameSegments = invocationOnMock.getArgument(0).toString().split("-");
                        if (nodeNameSegments.length < 2) {
                            return null;
                        } else {
                            return node(nodeNameSegments[1]);
                        }
                    }
                });

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

        parent = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnSingleRootNodeOnTraverse() {
        List<NodeWrapper<String>> result = parent.traverse(navigator,
                asList(node("1-p1"), node("2"), node("3-p2"), node("4-p1")));
        assertThat(result).containsExactly(node("p1"), node("p2"));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        doReturn(null).when(navigator).parentOf(ArgumentMatchers.<NodeWrapper<String>>any());

        List<NodeWrapper<String>> result = parent.traverse(navigator,
                asList(node("1-p1"), node("2"), node("3-p2"), node("4-p1")));
        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), eq(false)))
                .thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = parent.traverse(navigator,
                asList(node("1-p1"), node("2"), node("3-p2"), node("4-p1")));
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        parent.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(parent).hasToString("..[" + predicate1 + "][" + predicate2 + ']');
    }

}