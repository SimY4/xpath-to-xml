package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

public class DescendantOrSelfAxisResolverTest extends AbstractAxisResolverTest {

    @Before
    public void setUp() {
        axisResolver = new DescendantOrSelfAxisResolver(name, true);
    }

    @Test
    public void shouldReturnSelfWithAllDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode(), node("node11"),
                node("node1111"), node("node1112"), node("node12"), node("node1211"), node("node1212"), node(name));
    }

    @Test
    public void shouldReturnOnlyDescendantElements() {
        // given
        setUpResolvableAxis();
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(node("node11"),
                node("node1111"), node("node1112"), node("node12"), node("node1211"), node("node1212"), node(name));
    }

    @Test
    public void shouldReturnOnlySelfWhenThereAreNoChildren() {
        // given
        doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), true);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(parentNode.getNode());
    }

    @Test
    public void shouldReturnEmptyWhenThereAreNoChildren() {
        // given
        doReturn(emptyList()).when(navigator).elementsOf(parentNode.getNode());
        axisResolver = new DescendantOrSelfAxisResolver(new QName("*", "*"), false);

        // when
        View<TestNode> result = axisResolver.resolveAxis(new ViewContext<TestNode>(navigator, parentNode, false));

        // then
        assertThat((Iterable<?>) result).isEmpty();
    }

    @Override
    protected void setUpResolvableAxis() {
        doReturn(asList(node("node11"), node("node12"))).when(navigator).elementsOf(parentNode.getNode());
        doReturn(asList(node("node1111"), node("node1112"))).when(navigator).elementsOf(node("node11"));
        doReturn(asList(node("node1211"), node("node1212"), node(name))).when(navigator).elementsOf(node("node12"));
    }

}