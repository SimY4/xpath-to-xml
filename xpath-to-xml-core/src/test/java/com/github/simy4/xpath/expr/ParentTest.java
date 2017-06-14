package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ParentTest extends AbstractStepExprTest<Parent> {

    @Before
    public void setUp() {
        when(navigator.parentOf(node("node"))).thenReturn(node("parent"));

        expr = new Parent(asList(predicate1, predicate2));
    }

    @Test
    public void shouldReturnParentNodeOnTraverse() {
        // given
        setUpResolvableExpr();

        // when
        Set<NodeView<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), node("node"));

        // then
        assertThat(result).containsExactly(node("parent"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // given
        setUpUnresolvableExpr();

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), node("node"));
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