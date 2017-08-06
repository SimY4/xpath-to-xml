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

public class ElementTest extends AbstractStepExprTest<Element> {

    @Before
    public void setUp() {
        QName elem = new QName("elem");

        when(navigator.createElement(any(TestNode.class), eq(elem))).thenReturn(node("elem"));

        stepExpr = new Element(elem, asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchElementsFromAListOfChildNodes() {
        // given
        setUpResolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("elem"));
    }

    @Test
    public void shouldCreateElement() {
        // given
        setUpUnresolvableExpr();

        // when
        IterableNodeView<TestNode> result = stepExpr.resolve(new ViewContext<>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("elem"));
        verify(navigator).createElement(node("node"), new QName("elem"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardNamespace() {
        // given
        setUpUnresolvableExpr();
        stepExpr = new Element(new QName("*", "attr"), asList(predicate1, predicate2));

        // when
        consume(stepExpr.resolve(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForElementsWithWildcardLocalPart() {
        // given
        setUpUnresolvableExpr();
        stepExpr = new Element(new QName("http://www.example.com/my", "*", "my"), asList(predicate1, predicate2));

        // when
        consume(stepExpr.resolve(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateElement() {
        // given
        setUpUnresolvableExpr();
        when(navigator.createElement(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        consume(stepExpr.resolve(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(stepExpr).hasToString("elem[" + predicate1 + "][" + predicate2 + ']');
    }

    @Override
    void setUpResolvableExpr() {
        doReturn(asList(node("elem"), node("another-elem"))).when(navigator).elementsOf(parentNode.getNode());
        super.setUpResolvableExpr();
    }

}