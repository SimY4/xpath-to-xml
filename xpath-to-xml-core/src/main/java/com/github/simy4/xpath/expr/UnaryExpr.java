package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;

import java.io.Serializable;

public class UnaryExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final Expr valueExpr;

  public UnaryExpr(Expr valueExpr) {
    this.valueExpr = valueExpr;
  }

  @Override
  public <N extends Node> NumberView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    return new NumberView<>(
        -valueExpr.resolve(navigator, view, !view.hasNext() && greedy).toNumber());
  }

  @Override
  public String toString() {
    return "-(" + valueExpr + ')';
  }
}
