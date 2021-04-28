package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.io.Serializable;

public final class BooleanView<N extends Node> implements View<N>, Serializable {

  private static final BooleanView<?> FALSE = new BooleanView<Node>(false);
  private static final BooleanView<?> TRUE = new BooleanView<Node>(true);

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unchecked")
  public static <T extends Node> BooleanView<T> of(boolean bool) {
    return (BooleanView<T>) (bool ? TRUE : FALSE);
  }

  private final boolean bool;

  private BooleanView(boolean bool) {
    this.bool = bool;
  }

  @Override
  public int compareTo(View<N> other) {
    final boolean thatBool = other.toBoolean();
    return (bool == thatBool) ? 0 : (bool ? 1 : -1);
  }

  @Override
  public boolean toBoolean() {
    return bool;
  }

  @Override
  public double toNumber() {
    return bool ? 1.0 : 0.0;
  }

  @Override
  public String toString() {
    return Boolean.toString(bool);
  }

  @Override
  public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
    return visitor.visit(this);
  }

  private Object readResolve() {
    return of(bool);
  }
}
