package com.github.simy4.xpath.jdom.navigator.node;

import java.io.Serializable;

abstract class AbstractJDomNode<N extends Serializable> implements JDomNode, Serializable {

  private static final long serialVersionUID = 1L;

  private final N node;

  protected AbstractJDomNode(N node) {
    this.node = node;
  }

  protected final N getNode() {
    return node;
  }

  @Override
  public final boolean equals(Object o) {
    return (this == o
        || (o instanceof AbstractJDomNode && node.equals(((AbstractJDomNode<?>) o).node)));
  }

  @Override
  public final int hashCode() {
    return node.hashCode();
  }

  @Override
  public String toString() {
    return node.toString();
  }
}
