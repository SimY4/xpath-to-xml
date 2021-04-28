package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class PathExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final List<StepExpr> pathExpr;

  public PathExpr(List<StepExpr> pathExpr) {
    this.pathExpr = pathExpr;
  }

  @Override
  public <N extends Node> IterableNodeView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    final boolean newGreedy = !view.hasNext() && greedy;
    IterableNodeView<N> children = view;
    for (final StepExpr stepExpr : pathExpr) {
      children = children.flatMap(new StepResolver<N>(navigator, stepExpr, newGreedy));
    }
    return children;
  }

  @Override
  public String toString() {
    final Iterator<StepExpr> pathExprIterator = pathExpr.iterator();
    final StringBuilder stringBuilder = new StringBuilder();
    if (pathExprIterator.hasNext()) {
      stringBuilder.append(pathExprIterator.next());
      while (pathExprIterator.hasNext()) {
        stringBuilder.append('/').append(pathExprIterator.next());
      }
    }
    return stringBuilder.toString();
  }

  private static final class StepResolver<T extends Node>
      implements Function<NodeView<T>, IterableNodeView<T>> {

    private final Navigator<T> navigator;
    private final StepExpr stepExpr;
    private final boolean greedy;

    private StepResolver(Navigator<T> navigator, StepExpr stepExpr, boolean greedy) {
      this.navigator = navigator;
      this.stepExpr = stepExpr;
      this.greedy = greedy;
    }

    @Override
    public IterableNodeView<T> apply(NodeView<T> view) {
      return stepExpr.resolve(navigator, view, greedy);
    }
  }
}
