package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.view.NodeView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutEffectTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ExprContext<String>> contextCaptor;

    private Effect putEffect;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));

        putEffect = new PutEffect(expr);
    }

    @Test
    public void shouldGreedilyResolveExpr() {
        // when
        putEffect.perform(navigator);

        // then
        verify(expr).resolve(contextCaptor.capture(), eq(new NodeView<String>(node("xml"))));
        assertThat(contextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, true, 1, 0);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        // given
        when(expr.resolve(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeView<String>>any()))
                .thenThrow(XmlBuilderException.class);

        // then
        putEffect.perform(navigator);
    }

}