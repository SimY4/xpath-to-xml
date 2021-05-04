package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.io.Serializable;

public final class LiteralView<N extends Node> implements View<N>, Serializable {

  private static final long serialVersionUID = 1L;

  private final String literal;

  public LiteralView(String literal) {
    this.literal = literal;
  }

  @Override
  public int compareTo(View<N> other) {
    return literal.compareTo(other.toString());
  }

  @Override
  public boolean toBoolean() {
    return !literal.isEmpty();
  }

  @Override
  public double toNumber() {
    try {
      return Double.parseDouble(literal);
    } catch (NumberFormatException nfe) {
      return Double.NaN;
    }
  }

  @Override
  public String toString() {
    return literal;
  }

  @Override
  public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
    return visitor.visit(this);
  }
}
