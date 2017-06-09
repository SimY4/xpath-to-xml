package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttributeTest extends AbstractStepExprTest<Attribute> {

    @Before
    public void setUp() {
        QName attr = new QName("attr");

        when(navigator.createAttribute(attr)).thenReturn(node("attr"));

        expr = new Attribute(attr, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat(result).containsExactly(node("attr"));
    }

    @Test
    public void shouldCreateAttribute() {
        // given
        setUpUnresolvableExpr();

        // when
        Set<NodeWrapper<String>> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat(result).containsExactly(node("attr"));
        verify(navigator).createAttribute(new QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        expr = new Attribute(new QName("*", "attr"), Collections.<Expr>emptyList());

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        expr = new Attribute(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createAttribute(any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString("@attr[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        when(navigator.attributesOf(parentNode)).thenReturn(asList(node("attr"), node("another-attr")));
        super.setUpResolvableExpr();
    }

}