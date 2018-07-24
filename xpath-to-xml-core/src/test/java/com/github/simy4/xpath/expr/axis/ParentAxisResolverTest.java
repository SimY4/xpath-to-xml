package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import static com.github.simy4.xpath.util.EagerConsumer.consume;
import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ParentAxisResolverTest extends AbstractAxisResolverTest {

    @Before
    public void setUp() {
        axisResolver = new ParentAxisResolver(name);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // when
        consume(axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(axisResolver).hasToString("parent::" + name);
    }

    @Override
    protected void setUpResolvableAxis() {
        when(navigator.parentOf(parentNode.getNode())).thenReturn(node(name));
    }

}