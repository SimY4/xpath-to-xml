/*
 * Copyright 2017-2026 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.expr;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.IntFunction;

public class AxisStepExpr implements StepExpr, Serializable {

  private static final long serialVersionUID = 1L;

  @SuppressWarnings("serial")
  private final AxisResolver axisResolver;

  @SuppressWarnings("serial")
  private final Collection<Expr> predicates;

  public AxisStepExpr(AxisResolver axisResolver) {
    this(axisResolver, Collections.emptySet());
  }

  public AxisStepExpr(AxisResolver axisResolver, Collection<Expr> predicates) {
    this.axisResolver = axisResolver;
    this.predicates = predicates;
  }

  @Override
  public final <N extends Node> IterableNodeView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    final boolean newGreedy = !view.hasNext() && greedy;
    final IterableNodeView<N> result = axisResolver.resolveAxis(navigator, view, newGreedy);
    if (predicates.isEmpty()) {
      return result;
    }
    return resolvePredicates(navigator, view, result, newGreedy);
  }

  private <N extends Node> IterableNodeView<N> resolvePredicates(
      Navigator<N> navigator, NodeView<N> view, IterableNodeView<N> axis, boolean greedy)
      throws XmlBuilderException {
    IntFunction<NodeView<N>> nodeSupplier =
        position -> axisResolver.createAxisNode(navigator, view, position);
    for (Expr predicate : predicates) {
      final PredicateResolver<N> predicateResolver =
          new PredicateResolver<>(navigator, nodeSupplier, predicate, greedy);
      axis = axis.flatMap(predicateResolver);
      nodeSupplier = predicateResolver;
    }
    return axis;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder(axisResolver.toString());
    for (Expr predicate : predicates) {
      stringBuilder.append('[').append(predicate).append(']');
    }
    return stringBuilder.toString();
  }

  private static final class PredicateResolver<T extends Node>
      implements IntFunction<NodeView<T>>, Function<NodeView<T>, IterableNodeView<T>> {

    private final Navigator<T> navigator;
    private final IntFunction<NodeView<T>> parentNodeSupplier;
    private final Expr predicate;
    private final boolean greedy;
    private boolean resolved;

    PredicateResolver(
        Navigator<T> navigator,
        IntFunction<NodeView<T>> parentNodeSupplier,
        Expr predicate,
        boolean greedy) {
      this.navigator = navigator;
      this.parentNodeSupplier = parentNodeSupplier;
      this.predicate = predicate;
      this.greedy = greedy;
    }

    @Override
    public NodeView<T> apply(int position) throws XmlBuilderException {
      final NodeView<T> newNode = parentNodeSupplier.apply(position);
      if (!predicate
          .resolve(navigator, newNode, true)
          .visit(new PredicateVisitor<>(navigator, newNode, true))) {
        throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
      }
      return newNode;
    }

    @Override
    public IterableNodeView<T> apply(NodeView<T> view) {
      final IterableNodeView<T> result;
      final boolean greedy = view.isMarked() && this.greedy;
      if (predicate
          .resolve(navigator, view, greedy)
          .visit(new PredicateVisitor<>(navigator, view, greedy))) {
        resolved = true;
        result = view;
      } else if (greedy) {
        throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
      } else if (!view.hasNext() && !resolved && this.greedy) {
        result = apply(view.getPosition() + 1);
      } else {
        result = NodeSetView.empty();
      }
      return result;
    }
  }

  private static final class PredicateVisitor<T extends Node>
      extends AbstractViewVisitor<T, Boolean> {

    private final Navigator<T> navigator;
    private final NodeView<T> view;
    private final boolean greedy;

    PredicateVisitor(Navigator<T> navigator, NodeView<T> view, boolean greedy) {
      this.navigator = navigator;
      this.view = view;
      this.greedy = greedy;
    }

    @Override
    public Boolean visit(NumberView<T> numberView) throws XmlBuilderException {
      final double number = numberView.toNumber();
      if (0 == Double.compare(number, view.getPosition())) {
        view.mark();
        return true;
      } else if (greedy && number > view.getPosition()) {
        final T nodeToCopy = view.getNode();
        final T parent = navigator.parentOf(nodeToCopy);
        long numberOfNodesToCreate = (long) number - view.getPosition();
        do {
          final T copy = navigator.createElement(parent, nodeToCopy.getName());
          navigator.appendPrev(nodeToCopy, copy);
        } while (--numberOfNodesToCreate > 0);
        return true;
      } else {
        return false;
      }
    }

    @Override
    protected Boolean returnDefault(View<T> view) {
      return view.toBoolean();
    }
  }
}
