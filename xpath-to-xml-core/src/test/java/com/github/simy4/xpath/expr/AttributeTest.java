package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.utils.StringNode.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AttributeTest extends AbstractStepExprTest<Attribute> {

    @Before
    public void setUp() {
        QName attr = new QName("attr");

        when(navigator.createAttribute(ArgumentMatchers.<Node<String>>any(), eq(attr))).thenReturn(node("attr"));

        expr = new Attribute(attr, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, false, 3), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<String>(node("attr")));
    }

    @Test
    public void shouldCreateAttribute() {
        // given
        setUpUnresolvableExpr();

        // when
        NodeSetView<String> result = expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);

        // then
        assertThat((Iterable<?>) result).containsExactly(new NodeView<String>(node("attr")));
        verify(navigator).createAttribute(node("node"), new QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        expr = new Attribute(new QName("*", "attr"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        expr = new Attribute(new QName("http://www.example.com/my", "*", "my"), asList(predicate1, predicate2));

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createAttribute(ArgumentMatchers.<Node<String>>any(), any(QName.class)))
                .thenThrow(XmlBuilderException.class);

        // when
        expr.resolve(new ExprContext<String>(navigator, true, 1), parentNode);
    }

    @Test
    public void testToString() {
        assertThat(expr).hasToString("@attr[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        doReturn(asList(node("attr"), node("another-attr"))).when(navigator).attributesOf(parentNode.getNode());
        super.setUpResolvableExpr();
    }

}