package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import com.github.simy4.xpath.utils.ExprContextMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class AbstractStepExprTest<E extends StepExpr> {

    protected static final NodeWrapper<String> parentNode = node("node");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock protected Navigator<String> navigator;
    @Mock protected Expr predicate1;
    @Mock protected Expr predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;
    protected E expr;

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1).resolve(predicate1ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 1);

    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // given
        setUpUnresolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        setUpResolvableExpr();
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldSkipCreatingNodeIfContextForbids() {
        // given
        setUpResolvableExpr();
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(Collections.<NodeWrapper<String>>emptySet());

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 3), parentNode);

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldCreateNodeAndResolvePredicatesWhenStepExprIsUnresolvable() {
        // given
        if (this instanceof IdentityTest || this instanceof ParentTest) {
            // These steps are unresolvable
            expectedException.expect(XmlBuilderException.class);
        }

        setUpUnresolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1).resolve(predicate1ContextCaptor.capture(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        assertThat(predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, true, 1, 1);
        assertThat(predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, true, 1, 1);
    }

    @Test
    public void shouldCreateNodeAndResolvePredicatesWhenStepExprIsPartiallyResolvable() {
        // given
        if (this instanceof IdentityTest || this instanceof ParentTest) {
            // These steps are unresolvable
            expectedException.expect(XmlBuilderException.class);
        }

        setUpResolvableExpr();
        reset(predicate1, predicate2);
        when(predicate1.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1, times(2)).resolve(predicate1ContextCaptor.capture(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).resolve(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 1),
                        tuple(navigator, true, 1, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, true, 1, 1));
    }

    void setUpResolvableExpr() {
        when(predicate1.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.resolve(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });
    }

    void setUpUnresolvableExpr() {
        when(predicate1.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.resolve(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenAnswer(new Answer<Set<NodeWrapper<String>>>() {
                    @Override
                    public Set<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singleton(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });
    }

}
