package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.ExprContextMatcher;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class AbstractStepExprTest<E extends StepExpr> {

    protected static final NodeView<String> parentNode = new NodeView<String>(node("node"));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock protected Navigator<String> navigator;
    @Mock protected Predicate predicate1;
    @Mock protected Predicate predicate2;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<String>> predicate2ContextCaptor;

    protected E expr;

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1).match(predicate1ContextCaptor.capture(), ArgumentMatchers.<View<String>>any());
        verify(predicate2).match(predicate2ContextCaptor.capture(), ArgumentMatchers.<View<String>>any());
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
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        setUpResolvableExpr();
        when(predicate1.match(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(false);

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).isEmpty();
        verify(predicate2, never()).match(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<NodeView<String>>any());
    }

    @Test
    public void shouldSkipCreatingNodeIfContextForbids() {
        // given
        setUpResolvableExpr();
        when(predicate1.match(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(false);

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, true, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).isEmpty();
        verify(predicate2, never()).match(ArgumentMatchers.<ExprContext<String>>any(),
                ArgumentMatchers.<View<String>>any());
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
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1).match(predicate1ContextCaptor.capture(), ArgumentMatchers.<View<String>>any());
        verify(predicate2).match(predicate2ContextCaptor.capture(), ArgumentMatchers.<View<String>>any());
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
        when(predicate1.match(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<View<String>>any())).thenReturn(true);
        when(predicate2.match(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<View<String>>any())).thenReturn(true);

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, times(2)).match(predicate1ContextCaptor.capture(),
                ArgumentMatchers.<NodeView<String>>any());
        verify(predicate2).match(predicate2ContextCaptor.capture(), ArgumentMatchers.<View<String>>any());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(
                        tuple(navigator, false, 1, 1),
                        tuple(navigator, true, 2, 2));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(tuple(navigator, true, 1, 1));
    }

    void setUpResolvableExpr() {
        when(predicate1.match(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(true);
        when(predicate2.match(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<View<String>>any()))
                .thenReturn(true);
    }

    void setUpUnresolvableExpr() {
        when(predicate1.match(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<View<String>>any())).thenReturn(true);
        when(predicate2.match(ArgumentMatchers.argThat(ExprContextMatcher.<String>greedyContext()),
                ArgumentMatchers.<View<String>>any())).thenReturn(true);
    }

}
