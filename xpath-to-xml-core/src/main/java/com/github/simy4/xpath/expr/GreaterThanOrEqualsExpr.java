package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;

public class GreaterThanOrEqualsExpr extends AbstractOperationExpr {

  private static final long serialVersionUID = 1L;

  public GreaterThanOrEqualsExpr(Expr leftExpr, Expr rightExpr) {
    super(leftExpr, rightExpr);
  }

  @Override
  public <N extends Node> View<N> resolve(
      Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
      throws XmlBuilderException {
    boolean ge = 0 <= Double.compare(left.toNumber(), right.toNumber());
    if (!ge && greedy) {
      ge = left.visit(new EqualsExpr.EqualsVisitor<N>(navigator, right));
    }
    return BooleanView.of(ge);
  }

  @Override
  protected String operator() {
    return ">=";
  }
}
