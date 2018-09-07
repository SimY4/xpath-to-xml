package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class ParentAxisResolverTest extends AbstractAxisResolverTest {

    @BeforeEach
    void setUp() {
        axisResolver = new ParentAxisResolver(name);
    }

    @Test
    @DisplayName("Should throw on create node")
    void shouldThrowOnCreateNode() {
        // when
        assertThatThrownBy(() -> stream(axisResolver.resolveAxis(
                new ViewContext<>(navigator, parentNode, true)).spliterator(), false)
                .collect(Collectors.toList()))
                .isInstanceOf(XmlBuilderException.class);
    }

    @Test
    void testToString() {
        assertThat(axisResolver).hasToString("parent::" + name);
    }

    @Override
    void setUpResolvableAxis() {
        when(navigator.parentOf(parentNode.getNode())).thenReturn(node(name));
    }

}