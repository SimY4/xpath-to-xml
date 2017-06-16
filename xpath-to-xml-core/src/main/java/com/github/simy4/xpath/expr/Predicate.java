package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.view.View;

public interface Predicate {

    <N> boolean match(ExprContext<N> context, View<N> xml);

}
