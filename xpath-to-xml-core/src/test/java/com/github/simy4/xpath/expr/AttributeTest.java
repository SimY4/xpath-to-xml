package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.EagerConsumer.consume;
import static com.github.simy4.xpath.util.TestNode.node;
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

        when(navigator.createAttribute(any(TestNode.class), eq(attr))).thenReturn(node("attr"));

        stepExpr = new Attribute(attr, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("attr"));
    }

    @Test
    public void shouldCreateAttribute() {
        // given
        setUpUnresolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("attr"));
        verify(navigator).createAttribute(node("node"), new QName("attr"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        stepExpr = new Attribute(new QName("*", "attr"), asList(predicate1, predicate2));

        // when
        consume(stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        stepExpr = new Attribute(new QName("http://www.example.com/my", "*", "my"), asList(predicate1, predicate2));

        // when
        consume(stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createAttribute(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        consume(stepExpr.resolve(new ViewContext<TestNode>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(stepExpr).hasToString("@attr[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    protected void setUpResolvableExpr() {
        doReturn(asList(node("attr"), node("another-attr"))).when(navigator).attributesOf(parentNode.getNode());
        super.setUpResolvableExpr();
    }

}