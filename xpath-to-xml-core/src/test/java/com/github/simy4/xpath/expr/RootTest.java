package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.util.TestNode;
import com.github.simy4.xpath.view.NodeView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static com.github.simy4.xpath.util.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RootTest {

    private static final NodeView<TestNode> parentNode = new NodeView<>(node("node"));

    @Mock private Navigator<TestNode> navigator;

    private final StepExpr root = new Root();

    @BeforeEach
    void setUp() {
        when(navigator.root()).thenReturn(node("root"));
    }

    @Test
    @DisplayName("Should return single root node")
    void shouldReturnSingleRootNodeOnTraverse() {
        // when
        var result = root.resolve(navigator, parentNode, false);

        // then
        assertThat(result).extracting("node").containsExactly(node("root"));
    }

    @Test
    void testToString() {
        assertThat(root).hasToString("");
    }

}