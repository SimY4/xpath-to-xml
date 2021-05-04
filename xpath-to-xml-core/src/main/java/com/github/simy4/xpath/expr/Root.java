package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;

public class Root implements StepExpr, Serializable {

  private static final long serialVersionUID = 1L;

  @Override
  public <N extends Node> NodeView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) {
    return new NodeView<>(navigator.root());
  }

  @Override
  public String toString() {
    return "";
  }
}
