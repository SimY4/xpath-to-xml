/*
 * Copyright 2017-2021 Alex Simkin
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
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import java.io.Serializable;
import java.util.Collection;

public class AxisStepExpr implements StepExpr, Serializable {

  private static final long serialVersionUID = 1L;

  private final AxisResolver axisResolver;
  private final Collection<Expr> predicates;

  public AxisStepExpr(AxisResolver axisResolver, Collection<Expr> predicates) {
    this.axisResolver = axisResolver;
    this.predicates = predicates;
  }

  @Override
  public final <N extends Node> IterableNodeView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    final boolean newGreedy = !view.hasNext() && greedy;
    final IterableNodeView<N> result = axisResolver.resolveAxis(navigator, view, newGreedy);
    return resolvePredicates(navigator, view, result, newGreedy);
  }

  private <N extends Node> IterableNodeView<N> resolvePredicates(
      Navigator<N> navigator, NodeView<N> view, IterableNodeView<N> axis, boolean greedy)
      throws XmlBuilderException {
    IterableNodeView<N> result = axis;
    if (!predicates.isEmpty()) {
      NodeSupplier<N> nodeSupplier = new AxisNodeSupplier<N>(navigator, axisResolver, view);
      for (Expr predicate : predicates) {
        final PredicateExpr predicateExpr = new PredicateExpr(predicate);
        final PredicateResolver<N> predicateResolver =
            new PredicateResolver<N>(navigator, nodeSupplier, predicateExpr, greedy);
        result = result.flatMap(predicateResolver);
        nodeSupplier = predicateResolver;
      }
    }
    return result;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder(axisResolver.toString());
    for (Expr predicate : predicates) {
      stringBuilder.append(predicate);
    }
    return stringBuilder.toString();
  }

  private interface NodeSupplier<T extends Node> {

    NodeView<T> apply(int position) throws XmlBuilderException;
  }

  private static final class AxisNodeSupplier<T extends Node> implements NodeSupplier<T> {

    private final Navigator<T> navigator;
    private final AxisResolver axisResolver;
    private final NodeView<T> parent;

    AxisNodeSupplier(Navigator<T> navigator, AxisResolver axisResolver, NodeView<T> parent) {
      this.navigator = navigator;
      this.axisResolver = axisResolver;
      this.parent = parent;
    }

    @Override
    public NodeView<T> apply(int position) throws XmlBuilderException {
      return axisResolver.createAxisNode(navigator, parent, position);
    }
  }

  private static final class PredicateResolver<T extends Node>
      implements NodeSupplier<T>, Function<NodeView<T>, IterableNodeView<T>> {

    private final Navigator<T> navigator;
    private final NodeSupplier<T> parentNodeSupplier;
    private final Expr predicate;
    private final boolean greedy;
    private boolean resolved;

    PredicateResolver(
        Navigator<T> navigator,
        NodeSupplier<T> parentNodeSupplier,
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
      if (!predicate.resolve(navigator, newNode, true).toBoolean()) {
        throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
      }
      return newNode;
    }

    @Override
    public IterableNodeView<T> apply(NodeView<T> view) {
      final IterableNodeView<T> result;
      final boolean check = predicate.resolve(navigator, view, false).toBoolean();
      if (check) {
        result = view;
      } else if ((view.isNew() || view.isMarked()) && greedy) {
        if (!predicate.resolve(navigator, view, true).toBoolean()) {
          throw new XmlBuilderException("Unable to satisfy expression predicate: " + predicate);
        }
        result = view;
      } else if (!view.hasNext() && !resolved && greedy) {
        result = apply(view.getPosition() + 1);
      } else {
        result = NodeSetView.empty();
      }
      resolved |= check;
      return result;
    }
  }
}
