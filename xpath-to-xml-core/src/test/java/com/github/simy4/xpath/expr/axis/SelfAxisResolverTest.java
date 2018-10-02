package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.namespace.QName;
import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SelfAxisResolverTest {

    private static final NodeView<TestNode> node = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;

    private AxisResolver axisResolver;

    @BeforeEach
    void setUp() {
        axisResolver = new SelfAxisResolver(node.getNode().getName());
    }

    @Test
    @DisplayName("When axis traversable should return traversed nodes")
    void shouldReturnTarversedNodesIfAxisIsTraversable() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, node, false));

        // then
        assertThat(result).containsExactly(node);
    }

    @Test
    @DisplayName("When axis traversable should not call to create")
    void shouldNotCallToCreateIfAxisIsTraversable() {
        // given
        axisResolver = spy(axisResolver);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, node, true));

        // then
        assertThat(result).containsExactly(node);
        verify(axisResolver, never()).createAxisNode(any());
    }

    @Test
    @DisplayName("When axis is not traversable return empty")
    void shouldReturnEmptyIfAxisIsNotTraversable() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, node, false));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When axis is not traversable and greedy context and has next should return empty")
    void shouldReturnEmptyIfAxisIsNotTraversableGreedyAndHasNext() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(
                new ViewContext<>(navigator, node, true, true, 1));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw on create node")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowOnCreateNode() {
        // given
        axisResolver = new SelfAxisResolver(new QName("another-name"));

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, node, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("self::" + node);
    }

}