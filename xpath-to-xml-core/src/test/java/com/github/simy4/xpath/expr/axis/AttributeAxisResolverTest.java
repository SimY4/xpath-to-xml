package com.github.simy4.xpath.expr.axis;

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

public class AttributeAxisResolverTest extends AbstractAxisResolverTest {

    @Before
    public void setUp() {
        when(navigator.createAttribute(any(TestNode.class), eq(name))).thenReturn(node("name"));

        axisResolver = new AttributeAxisResolver(name);
    }

    @Test
    public void shouldCreateAttribute() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("name"));
        verify(navigator).createAttribute(node("node"), new QName("name"));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        axisResolver = new AttributeAxisResolver(new QName("*", "attr"));

        // when
        consume(axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        axisResolver = new AttributeAxisResolver(new QName("http://www.example.com/my", "*", "my"));

        // when
        consume(axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        // given
        when(navigator.createAttribute(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        consume(axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(axisResolver).hasToString("attribute::" + name);
    }

    @Override
    protected void setUpResolvableAxis() {
        doReturn(asList(node("name"), node("another-name"))).when(navigator).attributesOf(parentNode.getNode());
    }

}