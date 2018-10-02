package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.namespace.QName;
import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@MockitoSettings(strictness = Strictness.LENIENT)
class DescendantOrSelfAxisResolverTest extends AbstractAxisResolverTest {

    @BeforeEach
    void setUp() {
        axisResolver = new DescendantOrSelfAxisResolver(name, true);
    }

    @Test
    @DisplayName("When descendant-or-self should return self and descendant nodes")
    void shouldReturnSelfWithAllDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).extracting("node").containsExactly(parentNode.getNode(), node("node11"),
                node("node1111"), node("node1112"), node("node12"), node("node1211"), node("node1212"), node(name));
    }

    @Test
    @DisplayName("When descendant should return descendant nodes")
    void shouldReturnOnlyDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).extracting("node").containsExactly(node("node11"),
                node("node1111"), node("node1112"), node("node12"), node("node1211"), node("node1212"), node(name));
    }

    @Test
    @DisplayName("When descendant-or-self and there are no children should return self")
    void shouldReturnOnlySelfWhenThereAreNoChildren() {
        // given
        doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    @DisplayName("When descendant and there are no children should return empty")
    void shouldReturnEmptyWhenThereAreNoChildren() {
        // given
        doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should throw on create node")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowOnCreateNode() {
        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("descendant-or-self::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        doReturn(asList(node("node11"), node("node12"))).when(navigator).elementsOf(parentNode.getNode());
        doReturn(asList(node("node1111"), node("node1112"))).when(navigator).elementsOf(node("node11"));
        doReturn(asList(node("node1211"), node("node1212"), node(name))).when(navigator).elementsOf(node("node12"));
    }

}