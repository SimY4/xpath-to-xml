package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.EagerConsumer.consume;
import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

public class AncestorOrSelfAxisResolverTest extends AbstractAxisResolverTest {

    @Before
    public void setUp() {
        axisResolver = new AncestorOrSelfAxisResolver(name, true);
    }

    @Test
    public void shouldReturnSelfWithAllDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode(),
                node("parent1"), node("parent2"), node(name));
    }

    @Test
    public void shouldReturnOnlyDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(
                node("parent1"), node("parent2"), node(name));
    }

    @Test
    public void shouldReturnOnlySelfWhenThereAreNoChildren() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    public void shouldReturnEmptyWhenThereAreNoChildren() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // when
        consume(axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, true)));
    }

    @Test
    public void testToString() {
        assertThat(axisResolver).hasToString("ancestor-or-self::" + name);
    }

    @Override
    protected void setUpResolvableAxis() {
        doReturn(node("parent1")).when(navigator).parentOf(parentNode.getNode());
        doReturn(node("parent2")).when(navigator).parentOf(node("parent1"));
        doReturn(node(name)).when(navigator).parentOf(node("parent2"));
    }

}