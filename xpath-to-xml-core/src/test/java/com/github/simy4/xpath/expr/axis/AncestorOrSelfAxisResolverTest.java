package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
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
    void shouldReturnSelfWithAllDescendantElements() {
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
    void shouldReturnOnlyDescendantElements() {
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
    void shouldReturnOnlySelfWhenThereAreNoChildren() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    void shouldReturnEmptyWhenThereAreNoChildren() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new AncestorOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Test
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