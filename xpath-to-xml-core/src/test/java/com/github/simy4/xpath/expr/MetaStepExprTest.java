package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetaStepExprTest {

    @Mock private Navigator<String> navigator;
    @Mock private StepExpr stepExpr;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;

    private StepExpr metaStepExpr;

    @Before
    public void setUp() {

        metaStepExpr = new MetaStepExpr(stepExpr, asList(predicate1, predicate2));
    }

    @Test
    public void shouldTraverseStepAndThenFilterResultByApplyingPredicatesOneByOne() {
        when(stepExpr.traverse(navigator, singletonList(node("parent"))))
                .thenReturn(asList(node("child1"), node("child2")));
        when(predicate1.apply(navigator, node("child1"), false)).thenReturn(singletonList(node("child1[P1]")));
        when(predicate1.apply(navigator, node("child2"), false)).thenReturn(singletonList(node("child2[P1]")));
        when(predicate2.apply(navigator, node("child1"), false)).thenReturn(singletonList(node("child1[P2]")));

        List<NodeWrapper<String>> result = metaStepExpr.traverse(navigator, singletonList(node("parent")));
        assertThat(result).containsExactly(node("child1"));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        List<NodeWrapper<String>> result = metaStepExpr.traverse(navigator, singletonList(node("parent")));
        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(stepExpr.traverse(navigator, singletonList(node("parent"))))
                .thenReturn(asList(node("child1"), node("child2")));

        List<NodeWrapper<String>> result = metaStepExpr.traverse(navigator, singletonList(node("parent")));
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldCreateNodeByStepExprRulesAndApplyPredicatesToIt() {
        when(stepExpr.createNode(navigator)).thenReturn(node("newNode"));
        when(predicate1.apply(navigator, node("newNode"), true)).thenReturn(singletonList(node("newNode[P1]")));
        when(predicate2.apply(navigator, node("newNode"), true)).thenReturn(singletonList(node("newNode[P2]")));

        NodeWrapper<String> result = metaStepExpr.createNode(navigator);
        assertThat(result).isEqualTo(node("newNode"));
        verify(stepExpr).createNode(navigator);
        verify(predicate1).apply(navigator, node("newNode"), true);
        verify(predicate1).apply(navigator, node("newNode"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        when(stepExpr.createNode(ArgumentMatchers.<Navigator<String>>any())).thenThrow(XmlBuilderException.class);

        metaStepExpr.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(metaStepExpr).hasToString(stepExpr + "[" + predicate1 + "][" + predicate2 + "]");
    }

}