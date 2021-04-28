package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

public class AdditionExpr extends AbstractOperationExpr {

  private static final long serialVersionUID = 1L;

  public AdditionExpr(Expr leftExpr, Expr rightExpr) {
    super(leftExpr, rightExpr);
  }

  @Override
  protected <N extends Node> View<N> resolve(
      Navigator<N> navigator, View<N> left, View<N> right, boolean greedy) {
    return new NumberView<N>(left.toNumber() + right.toNumber());
  }

  @Override
  protected String operator() {
    return "+";
  }
}
