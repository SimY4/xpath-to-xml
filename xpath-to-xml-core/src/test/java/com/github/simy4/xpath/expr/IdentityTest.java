package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class IdentityTest extends AbstractStepExprTest<Identity> {

    @Before
    public void setUp() {
        stepExpr = new Identity(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnTheSameNodesOnResolve() {
        // given
        setUpResolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    public void testToString() {
        assertThat(stepExpr).hasToString(".[" + predicate1 + "][" + predicate2 + ']');
    }

}