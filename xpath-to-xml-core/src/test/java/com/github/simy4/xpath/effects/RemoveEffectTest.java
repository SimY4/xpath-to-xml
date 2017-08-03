package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.NodeView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveEffectTest {

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> contextCaptor;

    private Effect removeEffect;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));
        when(expr.resolve(ArgumentMatchers.<ExprContext<TestNode>>any()))
                .thenReturn(new NodeView<TestNode>(node("node")));

        removeEffect = new RemoveEffect(expr);
    }

    @Test
    public void shouldDetachGivenNodes() {
        // when
        removeEffect.perform(navigator);

        // then
        verify(expr).resolve(contextCaptor.capture());
        verify(navigator).remove(node("node"));
        assertThat((Object) contextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 0);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        // given
        doThrow(XmlBuilderException.class).when(navigator).remove(any(TestNode.class));

        // when
        removeEffect.perform(navigator);
    }

}