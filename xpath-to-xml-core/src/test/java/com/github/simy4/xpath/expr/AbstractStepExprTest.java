package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class AbstractStepExprTest<E extends StepExpr> extends AbstractExprTest<E> {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock protected Predicate predicate1;
    @Mock protected Predicate predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1).apply(predicate1ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).apply(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
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
        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(false);

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any());
    }

    @Test
    public void shouldSkipCreatingNodeIfContextForbids() {
        // given
        setUpResolvableExpr();
        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(false);

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 3), parentNode);

        // then
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<ExprContext<String>>any(),
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
        verify(predicate1).apply(predicate1ContextCaptor.capture(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).apply(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
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
        when(predicate1.apply(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);
        when(predicate2.apply(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat(result).isNotEmpty();
        verify(predicate1, times(2)).apply(predicate1ContextCaptor.capture(),
                ArgumentMatchers.<NodeWrapper<String>>any());
        verify(predicate2).apply(predicate2ContextCaptor.capture(), ArgumentMatchers.<NodeWrapper<String>>any());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 1),
                        tuple(navigator, true, 2, 2));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, true, 2, 2));
    }

    void setUpResolvableExpr() {
        when(predicate1.apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);
        when(predicate2.apply(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);
    }

    void setUpUnresolvableExpr() {
        when(predicate1.apply(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);
        when(predicate2.apply(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<NodeWrapper<String>>any())).thenReturn(true);
    }

}
