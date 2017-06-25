package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.Before;
import org.junit.Test;

import static com.github.simy4.xpath.utils.TestNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ParentTest extends AbstractStepExprTest<Parent> {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Before
    public void setUp() {
        when(navigator.parentOf(parentNode.getNode())).thenReturn(node("parent"));

        expr = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnParentNodeOnTraverse() {
        // given
        setUpResolvableExpr();

        // when
        NodeSetView<TestNode> result = expr.resolve(new ExprContext<TestNode>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<TestNode>(node("parent")));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // given
        setUpUnresolvableExpr();

        // when
        expr.resolve(new ExprContext<TestNode>(navigator, true, 1), parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString("..[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        when(navigator.parentOf(node("node"))).thenReturn(node("parent"));
        super.setUpResolvableExpr();
    }

}