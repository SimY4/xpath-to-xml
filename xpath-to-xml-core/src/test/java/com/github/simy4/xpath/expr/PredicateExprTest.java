package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.axis.SelfAxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.xml.namespace.QName;
import java.util.Collections;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Theories.class)
public class PredicateExprTest {

    @DataPoints("truthy")
    public static final Expr[] TRUTHY = {
            new LiteralExpr("2.0"),
            new EqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0)),
            new AxisStepExpr(new SelfAxisResolver(new QName("*", "*")), Collections.emptySet()),
    };

    @DataPoints("false")
    public static final Expr[] FALSE = {
            new LiteralExpr(""),
            new NotEqualsExpr(new NumberExpr(1.0), new NumberExpr(1.0))
    };

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private Navigator<TestNode> navigator;

    @Theory
    public void shouldReturnTrueForTruthyPredicate(@FromDataPoints("truthy") Expr truthy) {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        boolean result = new PredicateExpr(truthy).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(true);
    }

    @Theory
    public void shouldReturnFalseForNonGreedyFalsePredicate(@FromDataPoints("false") Expr falsy) {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), false);

        // when
        boolean result = new PredicateExpr(falsy).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void shouldReturnFalseOnGreedyFalseResolveAndNonNewNode() {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node")), true);

        // when
        boolean result = new PredicateExpr(new NumberExpr(3.0)).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    public void shouldPrependMissingNodesAndReturnTrueOnGreedyFalsePredicateAndNewNode() {
        // given
        ViewContext<TestNode> context = new ViewContext<>(navigator, new NodeView<>(node("node"), true), true);

        // when
        boolean result = new PredicateExpr(new NumberExpr(3.0)).resolve(context).toBoolean();

        // then
        assertThat(result).isEqualTo(true);
        verify(navigator, times(2)).prependCopy(node("node"));
    }

    @Test
    public void testToString() {
        // given
        Expr predicate = mock(Expr.class);

        // then
        assertThat(new PredicateExpr(predicate)).hasToString("[" + predicate + ']');
    }

}
