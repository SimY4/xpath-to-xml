package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveEffectTest {

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> contextCaptor;

    private Effect removeEffect;

    @BeforeEach
    void setUp() {
        when(expr.resolve(any())).thenReturn(new NodeView<>(node("node")));

        removeEffect = new RemoveEffect(expr);
    }

    @Test
    @DisplayName("Should detach resolved nodes")
    void shouldDetachResolvedNodes() {
        // when
        removeEffect.perform(navigator, node("xml"));

        // then
        verify(expr).resolve(contextCaptor.capture());
        verify(navigator).remove(node("node"));
        assertThat(contextCaptor.getValue()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(navigator, false, false, 1);
    }

    @Test
    @DisplayName("When exception should propagate")
    void shouldPropagateOnException() {
        // given
        var failure = new XmlBuilderException("Failure");
        doThrow(failure).when(navigator).remove(any(TestNode.class));

        // when
        assertThatThrownBy(() -> removeEffect.perform(navigator, node("xml"))).isSameAs(failure);
    }

}