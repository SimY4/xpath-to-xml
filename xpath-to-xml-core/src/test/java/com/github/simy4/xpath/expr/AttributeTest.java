package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttributeTest {

    @Mock private Navigator<String> navigator;
    @Mock private Expr predicate1;
    @Mock private Expr predicate2;

    private StepExpr attribute;

    @Before
    public void setUp() {
        when(navigator.attributesOf(node("node")))
                .thenReturn(asList(node("attr"), node("another-attr")));

        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P1]"));
                    }
                });
        when(predicate2.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean()))
                .thenAnswer(new Answer<List<NodeWrapper<String>>>() {
                    @Override
                    public List<NodeWrapper<String>> answer(InvocationOnMock invocationOnMock) {
                        return singletonList(node(invocationOnMock.getArgument(0) + "[P2]"));
                    }
                });

        attribute = new Attribute(new QName("attr"), asList(predicate1, predicate2));
    }

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        List<NodeWrapper<String>> result = attribute.traverse(navigator, singletonList(node("node")));
        assertThat(result).containsExactly(node("attr"));
    }

    @Test
    public void shouldShortCircuitWhenStepTraversalReturnsNothing() {
        when(navigator.attributesOf(node("node"))).thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = attribute.traverse(navigator, singletonList(node("node")));
        assertThat(result).isEmpty();
        verify(predicate1, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldShortCircuitWhenPredicateTraversalReturnsNothing() {
        when(predicate1.apply(eq(navigator), ArgumentMatchers.<NodeWrapper<String>>any(), eq(false)))
                .thenReturn(Collections.<NodeWrapper<String>>emptyList());

        List<NodeWrapper<String>> result = attribute.traverse(navigator, singletonList(node("node")));
        assertThat(result).isEmpty();
        verify(predicate2, never()).apply(ArgumentMatchers.<Navigator<String>>any(),
                ArgumentMatchers.<NodeWrapper<String>>any(), anyBoolean());
    }

    @Test
    public void shouldCreateAttribute() {
        when(navigator.createAttribute(new QName("attr"))).thenReturn(node("attr"));

        NodeWrapper<String> newAttribute = attribute.createNode(navigator);
        assertThat(newAttribute).isEqualTo(node("attr"));
        verify(navigator).createAttribute(new QName("attr"));
        verify(predicate1).apply(navigator, node("attr"), true);
        verify(predicate2).apply(navigator, node("attr"), true);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardNamespace() {
        attribute = new Attribute(new QName("*", "attr"), Collections.<Expr>emptyList());

        attribute.createNode(navigator);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldThrowForAttributesWithWildcardLocalPart() {
        attribute = new Attribute(new QName("http://www.example.com/my", "*", "my"),
                Collections.<Expr>emptyList());

        attribute.createNode(navigator);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        when(navigator.createAttribute(any(QName.class))).thenThrow(XmlBuilderException.class);

        attribute.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(attribute).hasToString("@attr[" + predicate1 + "][" + predicate2 + ']');
    }

}