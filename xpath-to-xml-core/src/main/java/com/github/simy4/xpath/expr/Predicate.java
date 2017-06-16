package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.view.View;

public interface Predicate {

    <N> boolean match(ExprContext<N> context, View<N> xml);

}
