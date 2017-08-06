package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import static com.github.simy4.xpath.util.EagerConsumer.consume;
import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ParentTest extends AbstractStepExprTest<Parent> {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Before
    public void setUp() {
        when(navigator.parentOf(parentNode.getNode())).thenReturn(node("parent"));

        stepExpr = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnParentNodeOnTraverse() {
        // given
        setUpResolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("parent"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // given
        setUpUnresolvableExpr();

        // when
        consume(stepExpr.resolve(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(stepExpr).hasToString("..[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        when(navigator.parentOf(node("node"))).thenReturn(node("parent"));
        super.setUpResolvableExpr();
    }

}