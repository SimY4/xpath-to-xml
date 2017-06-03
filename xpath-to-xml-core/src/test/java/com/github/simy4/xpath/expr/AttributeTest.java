package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.namespace.QName;
import java.util.List;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttributeTest {

    @Mock
    private Navigator<String> navigator;

    private final StepExpr attribute = new Attribute(new QName("attr"));

    @Test
    public void shouldMatchAttributesFromAListOfChildNodes() {
        when(navigator.attributesOf(node("node")))
                .thenReturn(asList(node("attr"), node("another-attr")));

        List<NodeWrapper<String>> result = attribute.traverse(navigator, singletonList(node("node")));
        assertThat(result).containsExactly(node("attr"));
    }

    @Test
    public void shouldCreateAttribute() {
        QName attributeName = new QName("attr");
        NodeWrapper<String> wrappedAttribute = node("attr");
        when(navigator.createAttribute(attributeName)).thenReturn(wrappedAttribute);

        NodeWrapper<String> newAttribute = attribute.createNode(navigator);
        assertThat(newAttribute).isEqualTo(wrappedAttribute);
        verify(navigator).createAttribute(attributeName);
    }

    @Test(expected = XmlBuilderException.class)
    public void shouldPropagateIfFailedToCreateAttribute() {
        when(navigator.createAttribute(any(QName.class))).thenThrow(XmlBuilderException.class);

        attribute.createNode(navigator);
    }

    @Test
    public void testToString() {
        assertThat(attribute).hasToString("@attr");
    }

}