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
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class AttributeAxisResolverTest extends AbstractAxisResolverTest {

    @BeforeEach
    void setUp() {
        when(navigator.createAttribute(any(TestNode.class), eq(name))).thenReturn(node("name"));

        axisResolver = new AttributeAxisResolver(name);
    }

    @Test
    @DisplayName("Should create attribute")
    void shouldCreateAttribute() {
        // when
        IterableNodeView<TestNode> result = axisResolver.resolveAxis(new ViewContext<>(navigator, parentNode, true));

        // then
        assertThat(result).extracting("node").containsExactly(node("name"));
        verify(navigator).createAttribute(node("node"), new QName("name"));
    }

    @Test
    @DisplayName("When wildcard namespace should throw")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowForAttributesWithWildcardNamespace() {
        // given
        axisResolver = new AttributeAxisResolver(new QName("*", "attr"));

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    @DisplayName("When wildcard local part should throw")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldThrowForAttributesWithWildcardLocalPart() {
        // given
        axisResolver = new AttributeAxisResolver(new QName("http://www.example.com/my", "*", "my"));

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    @DisplayName("When error should propagate")
    @SuppressWarnings("ReturnValueIgnored")
    void shouldPropagateIfFailedToCreateAttribute() {
        // given
        when(navigator.createAttribute(any(TestNode.class), any(QName.class))).thenThrow(XmlBuilderException.class);

        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("attribute::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        doReturn(asList(node("name"), node("another-name"))).when(navigator).attributesOf(parentNode.getNode());
    }

}