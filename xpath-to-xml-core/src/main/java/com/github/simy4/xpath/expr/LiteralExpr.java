package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.LiteralView;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;

public class LiteralExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final LiteralView<?> literal;

  public LiteralExpr(String literal) {
    this.literal = new LiteralView<>(literal);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <N extends Node> LiteralView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) {
    return (LiteralView<N>) literal;
  }

  @Override
  public String toString() {
    return "'" + literal.toString() + "'";
  }
}
