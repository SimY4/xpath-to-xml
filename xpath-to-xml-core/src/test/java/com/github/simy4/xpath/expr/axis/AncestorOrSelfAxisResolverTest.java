package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

class AncestorOrSelfAxisResolverTest extends AbstractAxisResolverTest {

    @BeforeEach
    void setUp() {
        axisResolver = new AncestorOrSelfAxisResolver(name, true);
    }

    @Test
    @DisplayName("When ancestor-or-self should return self and ancestor nodes")
    void shouldReturnSelfWithAllAncestorElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode(),
                node("parent1"), node("parent2"), node(name));
    }

    @Test
    @DisplayName("When ancestor should return ancestor nodes")
    void shouldReturnOnlyAncestorElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(
                node("parent1"), node("parent2"), node(name));
    }

    @Test
    @DisplayName("When ancestor-or-self and there are no ancestors should return self")
    void shouldReturnOnlySelfWhenThereAreNoAncestors() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    @DisplayName("When ancestor and there are no ancestors should return empty")
    void shouldReturnEmptyWhenThereAreNoAncestors() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
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
        assertThat(axisResolver).hasToString("ancestor-or-self::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        doReturn(node("parent1")).when(navigator).parentOf(parentNode.getNode());
        doReturn(node("parent2")).when(navigator).parentOf(node("parent1"));
        doReturn(node(name)).when(navigator).parentOf(node("parent2"));
    }

}