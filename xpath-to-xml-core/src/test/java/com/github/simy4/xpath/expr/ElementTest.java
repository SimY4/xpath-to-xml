package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.utils.TestNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementTest extends AbstractStepExprTest<Element> {

    @Before
    public void setUp() {
        QName elem = new QName("elem");

        when(navigator.createElement(any(TestNode.class), eq(elem))).thenReturn(node("elem"));

        expr = new Element(elem, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        NodeSetView<TestNode> result = expr.resolve(new ExprContext<TestNode>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<TestNode>(node("elem")));
    }

    @Test
    public void shouldCreateElement() {
        // given
        setUpUnresolvableExpr();

        // when
        NodeSetView<TestNode> result = expr.resolve(new ExprContext<TestNode>(navigator, true, 1), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<TestNode>(node("elem")));
        verify(navigator).createElement(node("node"), new QName("elem"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        expr = new Element(new QName("*", "attr"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<TestNode>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        expr = new Element(new QName("http://www.example.com/my", "*", "my"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<TestNode>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createElement(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        expr.resolve(new ExprContext<TestNode>(navigator, true, 1), parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        doReturn(asList(node("elem"), node("another-elem"))).when(navigator).elementsOf(parentNode.getNode());
        super.setUpResolvableExpr();
    }

}