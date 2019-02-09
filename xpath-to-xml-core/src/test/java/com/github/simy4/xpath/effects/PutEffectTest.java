package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PutEffectTest {

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr expr;

    private Effect putEffect;

    @BeforeEach
    void setUp() {
        when(expr.resolve(any(), any(), anyBoolean())).thenReturn(new NodeView<>(node("node")));

        putEffect = new PutEffect(expr);
    }

    @Test
    @DisplayName("Should greedily resolve expr")
    void shouldGreedilyResolveExpr() {
        // when
        putEffect.perform(navigator, node("xml"));

        // then
        verify(expr).resolve(eq(navigator), refEq(new NodeView<>(node("xml"))), eq(true));
    }

    @Test
    @DisplayName("Should greedily resolve literal expr")
    void shouldGreedilyResolveLiteralExpr() {
        // given
        when(expr.resolve(any(), any(), anyBoolean())).thenReturn(new LiteralView<>("literal"));

        // when
        putEffect.perform(navigator, node("xml"));

        // then
        verify(expr).resolve(eq(navigator), refEq(new NodeView<>(node("xml"))), eq(true));
    }

    @Test
    @DisplayName("When exception should propagate")
    void shouldPropagateOnException() {
        // given
        XmlBuilderException failure = new XmlBuilderException("Failure");
        when(expr.resolve(any(), any(), anyBoolean())).thenThrow(failure);

        // then
        assertThatThrownBy(() -> putEffect.perform(navigator, node("xml"))).isSameAs(failure);
    }

}