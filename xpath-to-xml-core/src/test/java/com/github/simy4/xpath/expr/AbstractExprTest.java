package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.NodeWrapper;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Set;

import static com.github.simy4.xpath.utils.StringNodeWrapper.node;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractExprTest<E extends Expr> {

    protected static final NodeWrapper<String> parentNode = node("node");

    @Mock protected Navigator<String> navigator;

    protected E expr;

    @Test
    public void shouldResolveMatchPredicateApply() {
        // given
        ExprContext<String> context = new ExprContext<String>(navigator, false, 3);

        // when
        Set<NodeWrapper<String>> result = expr.resolve(context, parentNode);

        // then
        assertThat(result.isEmpty()).isNotEqualTo(expr.asPredicate().apply(context, parentNode));
    }

}
