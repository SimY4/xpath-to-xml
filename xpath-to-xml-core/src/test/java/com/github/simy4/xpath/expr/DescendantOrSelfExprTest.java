package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.util.TestNode.node;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DescendantOrSelfExprTest {

    @Mock private Navigator<TestNode> navigator;

    private final Expr descendantOrSelf = new DescendantOrSelfExpr();

    @Before
    public void setUp() {
        doReturn(asList(node("node11"), node("node12"))).when(navigator).elementsOf(node("node"));
        doReturn(asList(node("node1111"), node("node1112"))).when(navigator).elementsOf(node("node11"));
        doReturn(asList(node("node1211"), node("node1212"))).when(navigator).elementsOf(node("node12"));
    }

    @Test
    public void shouldReturnSelfWithAllDescendantElements() {
        // given
        TestNode self = node("node");

        // when
        View<TestNode> result = descendantOrSelf.resolve(new ViewContext<>(navigator, new NodeView<>(self), false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(self, node("node11"),
                node("node1111"), node("node1112"), node("node12"), node("node1211"), node("node1212"));
    }

    @Test
    public void shouldReturnOnlySelfWhenThereAreNoChildren() {
        // given
        TestNode self = node("node");
        doReturn(emptyList()).when(navigator).elementsOf(self);

        // when
        View<TestNode> result = descendantOrSelf.resolve(new ViewContext<>(navigator, new NodeView<>(self), false));

        // then
        assertThat((Iterable<?>) result).extracting("node").containsExactly(self);
    }

    @Test
    public void shouldAlwaysReturnTrueOnMatch() {
        assertThat(descendantOrSelf.test(new ViewContext<>(navigator, new NodeView<>(node("node")), false))).isTrue();
    }

}