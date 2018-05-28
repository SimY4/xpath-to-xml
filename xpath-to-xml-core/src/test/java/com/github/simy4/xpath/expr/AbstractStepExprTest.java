package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Collections;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class AbstractStepExprTest<E extends StepExpr> {

    protected static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock protected Navigator<TestNode> navigator;
    @Mock protected Expr predicate1;
    @Mock protected Expr predicate2;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate1ContextCaptor;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> predicate2ContextCaptor;

    protected E stepExpr;

    @Before
    public void setUp() {
        when(predicate1.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
        when(predicate2.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
    }

    @Test
    public void shouldMatchNodeViaPredicatesChainFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1).resolve(predicate1ContextCaptor.capture());
        verify(predicate2).resolve(predicate2ContextCaptor.capture());
        assertThat((Object) predicate1ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
        assertThat((Object) predicate2ContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 1);
    }

    @Test
    public void shouldReturnNodesResolvedByStepExprOnly() throws NoSuchFieldException {
        // given
        setUpResolvableExpr();
        Field stepExprPredicatesField = stepExpr.getClass().getSuperclass().getDeclaredField("predicates");
        FieldSetter.setField(stepExpr, stepExprPredicatesField, Collections.emptyList());

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, never()).resolve(any(ViewContext.class));
        verify(predicate2, never()).resolve(any(ViewContext.class));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        // given
        setUpUnresolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        // given
        setUpResolvableExpr();
        when(predicate1.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
        verify(predicate2, never()).resolve(any(ViewContext.class));
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
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, times(2)).resolve(predicate1ContextCaptor.capture());
        verify(predicate2, times(2)).resolve(predicate2ContextCaptor.capture());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 1));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 1));
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
        when(predicate1.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
        when(predicate1.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
        when(predicate2.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        verify(predicate1, times(2)).resolve(predicate1ContextCaptor.capture());
        verify(predicate2, times(2)).resolve(predicate2ContextCaptor.capture());
        assertThat(predicate1ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 2));
        assertThat(predicate2ContextCaptor.getAllValues()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(
                        tuple(navigator, false, false, 1),
                        tuple(navigator, true, false, 1));
    }

    protected void setUpResolvableExpr() {
        when(predicate1.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(true));
    }

    protected void setUpUnresolvableExpr() {
        when(predicate1.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
        when(predicate1.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));
        when(predicate2.resolve(any(ViewContext.class))).thenReturn(BooleanView.of(false));
        when(predicate2.resolve(argThat(greedyContext()))).thenReturn(BooleanView.of(true));
    }

    private ArgumentMatcher<ViewContext> greedyContext() {
        return new ArgumentMatcher<ViewContext>() {
            @Override
            public boolean matches(ViewContext context) {
                return context.isGreedy();
            }
        };
    }

}
