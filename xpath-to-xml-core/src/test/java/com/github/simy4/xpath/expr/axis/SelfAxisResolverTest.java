package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.EagerConsumer.consume;
import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SelfAxisResolverTest {

    private static final NodeView<TestNode> node = new NodeView<TestNode>(node("node"));

    @Mock private Navigator<TestNode> navigator;

    private AxisResolver axisResolver;

    @Before
    public void setUp() {
        axisResolver = new SelfAxisResolver(node.getNode().getName());
    }

    @Test
    public void shouldReturnTarversedNodesIfAxisIsTraversable() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, node, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        assertThat((Iterable<?>) result).containsExactly(node);
    }

    @Test
    public void shouldNotCallToCreateIfAxisIsTraversable() {
        // given
        axisResolver = spy(axisResolver);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, node, true));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        assertThat((Iterable<?>) result).containsExactly(node);
        verify(axisResolver, never()).createAxisNode(any(ViewContext.class));
    }

    @Test
    public void shouldReturnEmptyIfAxisIsNotTraversable() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, node, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyIfAxisIsNotTraversableGreedyAndHasNext() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, node, true, true, 1));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowOnCreateNode() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        consume(axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, node, true)));
    }

    @Test
    public void testToString() {
        assertThat(axisResolver).hasToString("self::" + node);
    }

}