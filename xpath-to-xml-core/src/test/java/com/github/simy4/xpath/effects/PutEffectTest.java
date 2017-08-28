package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutEffectTest {

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ViewContext<TestNode>> contextCaptor;

    private Effect putEffect;

    @Before
    public void setUp() {
        when(expr.resolve(any(ViewContext.class))).thenReturn(new NodeView<TestNode>(node("node")));

        putEffect = new PutEffect(expr);
    }

    @Test
    public void shouldGreedilyResolveExpr() {
        // when
        putEffect.perform(navigator, node("xml"));

        // then
        verify(expr).resolve(contextCaptor.capture());
        assertThat((Object) contextCaptor.getValue()).extracting("navigator", "greedy", "hasNext", "position")
                .containsExactly(navigator, true, false, 1);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        // given
        when(expr.resolve(any(ViewContext.class))).thenThrow(XmlBuilderException.class);

        // then
        putEffect.perform(navigator, node("xml"));
    }

}