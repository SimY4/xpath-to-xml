package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class IdentityTest extends AbstractStepExprTest<Identity> {

    @Before
    public void setUp() {
        expr = new Identity(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnTheSameNodesOnResolve() {
        // given
        setUpResolvableExpr();

        // when
        Set<NodeView<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).containsExactly(parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString(".[" + predicate1 + "][" + predicate2 + ']');
    }

}