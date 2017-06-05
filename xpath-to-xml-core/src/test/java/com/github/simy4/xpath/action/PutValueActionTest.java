package com.github.simy4.xpath.action;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.ExprContext;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedHashSet;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PutValueActionTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;

    private Action putValueAction;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));
        when(expr.apply(ArgumentMatchers.<ExprContext<String>>any(), eq(node("xml")), eq(true)))
                .thenReturn(new LinkedHashSet<NodeWrapper<String>>(
                        asList(node("1"), node("2"), node("3"))));

        putValueAction = new PutValueAction(expr, "value");
    }

    @Test
    public void shouldPutValueToAllResolvedNodes() {
        putValueAction.perform(navigator);
        verify(navigator).setText(node("1"), "value");
        verify(navigator).setText(node("2"), "value");
        verify(navigator).setText(node("3"), "value");
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        doThrow(XmlBuilderException.class).when(navigator)
                .setText(ArgumentMatchers.<NodeWrapper<String>>any(), anyString());
        putValueAction.perform(navigator);
    }

}