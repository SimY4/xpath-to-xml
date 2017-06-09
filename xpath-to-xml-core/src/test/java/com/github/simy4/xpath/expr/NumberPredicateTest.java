package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer1;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NumberPredicateTest {

    @Mock
    private Navigator<String> navigator;

    private final Predicate numberPredicate = new NumberExpr(3.0);

    @Before
    public void setUp() throws Exception {
        when(navigator.clone(ArgumentMatchers.<NodeWrapper<String>>any()))
                .thenAnswer(AdditionalAnswers.answer(new Answer1<NodeWrapper<String>, NodeWrapper<String>>() {
                    @Override
                    public NodeWrapper<String> answer(NodeWrapper<String> toClone) {
                        return node("clone-" + toClone);
                    }
                }));
    }

    @Test
    public void shouldReturnTrueWhenPositionMatchNumber() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);
        context.advance();
        context.advance();
        context.advance();

        // when
        boolean result = numberPredicate.apply(context, node("node"));

        // then
        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenPositionDoesntMatchNumberAndContextPredicateShouldNotCreate() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 2);
        context.advance();
        context.advance();

        // when
        boolean result = numberPredicate.apply(context, node("node"));

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenPositionDoesntMatchNumberAndNodeIsNotLast() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);
        context.advance();

        // when
        boolean result = numberPredicate.apply(context, node("node"));

        // then
        assertThat(result).isFalse();
    }

    @Test
    public void shouldPrependMissingNodesAndReturnTrueOnGreedyApplication() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, true, 1);
        context.advance();

        // when
        boolean result = numberPredicate.apply(context, node("node"));

        // then
        assertThat(result).isTrue();
        verify(navigator, times(2)).clone(node("node"));
        verify(navigator, times(2))
                .prepend(eq(node("node")), ArgumentMatchers.<NodeWrapper<String>>any());
    }

}
