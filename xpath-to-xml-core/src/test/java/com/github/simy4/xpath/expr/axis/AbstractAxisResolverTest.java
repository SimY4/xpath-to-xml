package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractAxisResolverTest {

    protected static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));
    protected static final QName name = new QName("name");

    @Mock protected Navigator<TestNode> navigator;

    protected AxisResolver axisResolver;

    @Test
    public void shouldReturnTarversedNodesIfAxisIsTraversable() {
        // given
        setUpResolvableAxis();

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node(name));
    }

    @Test
    public void shouldNotCallToCreateIfAxisIsTraversable() {
        // given
        setUpResolvableAxis();
        axisResolver = spy(axisResolver);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).isNotEmpty();
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node(name));
        verify(axisResolver, never()).createAxisNode(ArgumentMatchers.<ViewContext<TestNode>>any());
    }

    @Test
    public void shouldReturnEmptyIfAxisIsNotTraversable() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyIfAxisIsNotTraversableGreedyAndHasNext() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<TestNode>(navigator, parentNode, true, true, 1));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    protected abstract void setUpResolvableAxis();

}
