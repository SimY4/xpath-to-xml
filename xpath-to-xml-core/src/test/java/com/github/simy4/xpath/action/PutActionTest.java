package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutActionTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;

    private Action putAction;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));

        putAction = new PutAction(expr);
    }

    @Test
    public void shouldGreedilyResolveExpr() {
        putAction.perform(navigator);
        verify(expr).apply(navigator, node("xml"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        when(expr.apply(ArgumentMatchers.<Navigator<String>>any(), ArgumentMatchers.<NodeWrapper<String>>any(),
                anyBoolean())).thenThrow(XmlBuilderException.class);
        putAction.perform(navigator);
    }

}