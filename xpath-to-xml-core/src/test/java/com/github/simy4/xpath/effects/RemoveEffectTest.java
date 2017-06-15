package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeSetView;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNodeView.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveEffectTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ExprContext<String>> contextCaptor;

    private Effect removeEffect;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));
        when(expr.resolve(ArgumentMatchers.<ExprContext<String>>any(), eq(node("xml"))))
                .thenReturn(NodeSetView.singleton(node("node")));

        removeEffect = new RemoveEffect(expr);
    }

    @Test
    public void shouldDetachGivenNodes() {
        // when
        removeEffect.perform(navigator);

        // then
        verify(expr).resolve(contextCaptor.capture(), eq(node("xml")));
        verify(navigator).remove(node("node"));
        assertThat(contextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        // given
        doThrow(XmlBuilderException.class).when(navigator).remove(ArgumentMatchers.<NodeView<String>>any());

        // when
        removeEffect.perform(navigator);
    }

}