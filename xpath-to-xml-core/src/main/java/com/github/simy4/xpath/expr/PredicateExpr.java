/*
 * Copyright 2018-2021 Alex Simkin
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
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;

public class PredicateExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final Expr predicate;

  public PredicateExpr(Expr predicate) {
    this.predicate = predicate;
  }

  @Override
  public <N extends Node> BooleanView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    return predicate
        .resolve(navigator, view, greedy)
        .visit(new PredicateVisitor<N>(navigator, view, greedy));
  }

  @Override
  public String toString() {
    return "[" + predicate + "]";
  }

  private static final class PredicateVisitor<T extends Node>
      extends AbstractViewVisitor<T, BooleanView<T>> {

    private final Navigator<T> navigator;
    private final NodeView<T> view;
    private final boolean greedy;

    private PredicateVisitor(Navigator<T> navigator, NodeView<T> view, boolean greedy) {
      this.navigator = navigator;
      this.view = view;
      this.greedy = greedy;
    }

    @Override
    public BooleanView<T> visit(NumberView<T> numberView) throws XmlBuilderException {
      final double number = numberView.toNumber();
      if (0 == Double.compare(number, view.getPosition())) {
        view.mark();
        return BooleanView.of(true);
      } else if (greedy && number > view.getPosition()) {
        final T node = view.getNode();
        long numberOfNodesToCreate = (long) number - view.getPosition();
        do {
          navigator.prependCopy(node);
        } while (--numberOfNodesToCreate > 0);
        return BooleanView.of(true);
      } else {
        return BooleanView.of(false);
      }
    }

    @Override
    protected BooleanView<T> returnDefault(View<T> view) throws XmlBuilderException {
      return BooleanView.of(view.toBoolean());
    }
  }
}
