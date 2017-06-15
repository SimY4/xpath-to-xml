package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementTest extends AbstractStepExprTest<Element> {

    @Before
    public void setUp() {
        when(navigator.createElement(new QName("elem"))).thenReturn(node("elem"));

        expr = new Element(new QName("elem"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(node("elem"));
    }

    @Test
    public void shouldCreateElement() {
        // given
        setUpUnresolvableExpr();

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(node("elem"));
        verify(navigator).createElement(new QName("elem"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        expr = new Element(new QName("*", "attr"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        expr = new Element(new QName("http://www.example.com/my", "*", "my"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createElement(any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        when(navigator.elementsOf(parentNode)).thenReturn(asList(node("elem"), node("another-elem")));
        super.setUpResolvableExpr();
    }

}