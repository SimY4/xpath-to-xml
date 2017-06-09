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

import java.util.LinkedHashSet;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoveActionTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr expr;
    @Captor private ArgumentCaptor<ExprContext<String>> contextCaptor;

    private Action removeAction;

    @Before
    public void setUp() {
        when(navigator.xml()).thenReturn(node("xml"));
        when(expr.resolve(ArgumentMatchers.<ExprContext<String>>any(), eq(node("xml"))))
                .thenReturn(new LinkedHashSet<NodeWrapper<String>>(asList(node("1"), node("2"), node("3"))));

        removeAction = new RemoveAction(expr);
    }

    @Test
    public void shouldDetachGivenNodes() {
        // when
        removeAction.perform(navigator);

        // then
        verify(expr).resolve(contextCaptor.capture(), eq(node("xml")));
        verify(navigator).remove(node("1"));
        verify(navigator).remove(node("2"));
        verify(navigator).remove(node("3"));
        assertThat(contextCaptor.getValue()).extracting("navigator", "greedy", "size", "position")
                .containsExactly(navigator, false, 1, 0);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateOnAnyException() {
        // given
        doThrow(XmlBuilderException.class).when(navigator).remove(ArgumentMatchers.<NodeWrapper<String>>any());

        // when
        removeAction.perform(navigator);
    }

}