package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.helpers.SerializationHelper;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.namespace.QName;

import java.io.IOException;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
abstract class AbstractAxisResolverTest {

    static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));
    static final QName name = new QName("name");

    @Mock Navigator<TestNode> navigator;

    AxisResolver axisResolver;

    @Test
    @DisplayName("When axis traversable should return traversed nodes")
    void shouldReturnTraversedNodesIfAxisIsTraversable() {
        // given
        setUpResolvableAxis();

        // when
        var result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).extracting("node").containsExactly(node(name));
    }

    @Test
    @DisplayName("When axis traversable should not call to create")
    void shouldNotCallToCreateIfAxisIsTraversable() {
        // given
        setUpResolvableAxis();
        axisResolver = spy(axisResolver);

        // when
        var result = axisResolver.resolveAxis(navigator, parentNode, true);

        // then
        assertThat(result).isNotEmpty();
        verify(axisResolver, never()).createAxisNode(any(), any(), anyInt());
    }

    @Test
    @DisplayName("When axis is not traversable return empty")
    void shouldReturnEmptyIfAxisIsNotTraversable() {
        // when
        var result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should serialize and deserialize axis")
    void shouldSerializeAndDeserializeAxis() throws IOException, ClassNotFoundException {
        // when
        AxisResolver deserializedAxis = SerializationHelper.serializeAndDeserializeBack(axisResolver);

        // then
        assertThat(deserializedAxis).isEqualToComparingFieldByFieldRecursively(axisResolver);
    }

    abstract void setUpResolvableAxis();

}
