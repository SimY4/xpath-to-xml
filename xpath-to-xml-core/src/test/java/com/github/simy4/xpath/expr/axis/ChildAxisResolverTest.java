package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ChildAxisResolverTest extends AbstractAxisResolverTest {

    @BeforeEach
    void setUp() {
        when(navigator.createElement(any(TestNode.class), eq(name))).thenReturn(node("name"));

        axisResolver = new ChildAxisResolver(name);
    }

    @Test
    void shouldCreateElement() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("name"));
        verify(navigator).createElement(node("node"), new QName("name"));
    }

    @Test
    void shouldThrowForElementsWithWildcardNamespace() {
        // given
        axisResolver = new ChildAxisResolver(new QName("*", "attr"));

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldThrowForElementsWithWildcardLocalPart() {
        // given
        axisResolver = new ChildAxisResolver(new QName("http://www.example.com/my", "*", "my"));

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void shouldPropagateIfFailedToCreateElement() {
        // given
        when(navigator.createElement(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("child::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        doReturn(asList(node("name"), node("another-name"))).when(navigator).elementsOf(parentNode.getNode());
    }

}