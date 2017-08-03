package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.expr.operators.Operator;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.utils.TestNode;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.simy4.xpath.utils.TestNode.node;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OperationExprTest {

    private static final NodeView<TestNode> parentNode = new NodeView<TestNode>(node("node"));

    @Mock private Navigator<TestNode> navigator;
    @Mock private Expr leftExpr;
    @Mock private Expr rightExpr;
    @Mock private Operator operator;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> leftExprContextCaptor;
    @Captor private ArgumentCaptor<ExprContext<TestNode>> rightExprContextCaptor;

    private Expr comparisonExpr;

    @Before
    public void setUp() {
        when(operator.resolve(ArgumentMatchers.<ExprContext<TestNode>>any(), ArgumentMatchers.<View<TestNode>>any(),
                ArgumentMatchers.<View<TestNode>>any())).thenReturn(BooleanView.<TestNode>of(false));

        comparisonExpr = new OperationExpr(leftExpr, rightExpr, operator);
    }

    @Test
    public void shouldReturnOperatorResolutionResult() {
        // given
        when(operator.resolve(ArgumentMatchers.<ExprContext<TestNode>>any(), ArgumentMatchers.<View<TestNode>>any(),
                ArgumentMatchers.<View<TestNode>>any())).thenReturn(BooleanView.<TestNode>of(true));
        ExprContext<TestNode> context = new ExprContext<TestNode>(navigator, false, parentNode);
        context.next();

        // when
        View<TestNode> result = comparisonExpr.resolve(context);

        // then
        verify(leftExpr).resolve(leftExprContextCaptor.capture());
        verify(rightExpr).resolve(rightExprContextCaptor.capture());
        assertThat(result).isEqualTo(BooleanView.<TestNode>of(true));
        assertThat((Object) leftExprContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 0);
        assertThat((Object) rightExprContextCaptor.getValue()).extracting("navigator", "greedy", "position")
                .containsExactly(navigator, false, 0);
    }

    @Test
    public void testToString() {
        assertThat(comparisonExpr).hasToString(leftExpr.toString() + operator.toString() + rightExpr.toString());
    }

}