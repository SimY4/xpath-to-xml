package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutActionTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ExprContext<String>> contextCaptor;

    private Action putAction;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));

        putAction = new PutAction(expr);
    }

    @Test
    public void shouldGreedilyResolveExpr() {
        putAction.perform(navigator);
        verify(expr).apply(contextCaptor.capture(), eq(node("xml")), eq(true));
        assertThat(contextCaptor.getValue()).isEqualTo(new ExprContext<String>(navigator, 1, 1));
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        when(expr.apply(ArgumentMatchers.<ExprContext<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                anyBoolean())).thenThrow(XmlBuilderException.class);
        putAction.perform(navigator);
    }

}