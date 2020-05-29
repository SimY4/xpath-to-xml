package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.xml.namespace.QName;

import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@MockitoSettings(strictness = Strictness.LENIENT)
class PrecedingSiblingAxisResolverTest extends AbstractAxisResolverTest {
    private static final TestNode parentParent = node("parent");

    @BeforeEach
    void setUp() {
        when(navigator.createElement(any(TestNode.class), eq(name))).thenReturn(node(name.getLocalPart()));

        axisResolver = new PrecedingSiblingAxisResolver(name, true);
    }

    @Test
    @DisplayName("When preceding-sibling should return only preceding sibling nodes")
    void shouldReturnOnlyPrecedingSiblingElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new PrecedingSiblingAxisResolver(new QName("*", "*"), true);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).extracting("node").containsExactly(node(name));
    }

    @Test
    @DisplayName("When preceding should return all preceding sibling nodes")
    void shouldReturnAllPrecedingElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new PrecedingSiblingAxisResolver(new QName("*", "*"), false);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).extracting("node").containsExactly(node(name), node("node1211"),
                node("node1212"));
    }

    @Test
    @DisplayName("When there is no parent should return empty")
    void shouldReturnEmptyWhenThereIsNoParent() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new PrecedingSiblingAxisResolver(new QName("*", "*"), true);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("When following and there is no parent should return empty")
    void shouldReturnEmptyWhenFollowingAndThereIsNoParent() {
        // given
        doReturn(null).when(navigator).parentOf(parentNode.getNode());
        axisResolver = new PrecedingSiblingAxisResolver(new QName("*", "*"), false);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, false);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create element")
    void shouldCreateElement() {
        // given
        setUpResolvableAxis();
        doReturn(asList(node("node11"), parentNode.getNode())).when(navigator).elementsOf(parentParent);

        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(navigator, parentNode, true);

        // then
        assertThat((Object) result).extracting("node", "position").containsExactly(node("name"), 1);
        verify(navigator).createElement(parentParent, name);
        verify(navigator).appendPrev(parentNode.getNode(), node("name"));
    }

    @Test
    @DisplayName("When wildcard namespace should throw")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowForElementsWithWildcardNamespace() {
        // given
        setUpResolvableAxis();
        doReturn(asList(node("node11"), parentNode.getNode())).when(navigator).elementsOf(parentParent);
        axisResolver = new PrecedingSiblingAxisResolver(new QName("*", "attr"), true);

        // when
        assertThatThrownBy(() ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                        .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    @DisplayName("When wildcard local part should throw")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowForElementsWithWildcardLocalPart() {
        // given
        setUpResolvableAxis();
        doReturn(asList(node("node11"), parentNode.getNode())).when(navigator).elementsOf(parentParent);
        axisResolver = new PrecedingSiblingAxisResolver(new QName("http://www.example.com/my", "*", "my"), true);

        // when
        assertThatThrownBy(() ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                        .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    @DisplayName("Should throw on append node")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowOnAppendNode() {
        // given
        when(navigator.parentOf(parentNode.getNode())).thenReturn(parentParent);
        doThrow(XmlBuilderException.class).when(navigator).appendPrev(any(TestNode.class), any(TestNode.class));

        // when
        assertThatThrownBy(() ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                        .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    @DisplayName("When error should propagate")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldPropagateIfFailedToCreateElement() {
        // given
        when(navigator.parentOf(parentNode.getNode())).thenReturn(parentParent);
        when(navigator.createElement(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        assertThatThrownBy(() ->
                stream(axisResolver.resolveAxis(navigator, parentNode, true).spliterator(), false)
                        .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("preceding-sibling::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        doReturn(parentParent).when(navigator).parentOf(parentNode.getNode());
        doReturn(asList(node(name), parentNode.getNode(), node("node11"))).when(navigator).elementsOf(parentParent);
        doReturn(asList(node("node1111"), node("node1112"))).when(navigator).elementsOf(node("node11"));
        doReturn(asList(node("node1211"), node("node1212"))).when(navigator).elementsOf(node(name));
    }
}