package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeSetView;
import org.junit.Before;
import org.junit.Test;

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
        NodeSetView<TestNode> result = expr.resolve(new ExprContext<TestNode>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString(".[" + predicate1 + "][" + predicate2 + ']');
    }

}