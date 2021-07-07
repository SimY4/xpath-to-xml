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
package com.github.simy4.xpath.effects;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.view.AbstractViewVisitor;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;
import com.github.simy4.xpath.view.ViewVisitor;

import java.io.Serializable;

public class PutEffect implements Effect, Serializable {

  private static final long serialVersionUID = 1L;

  private static final ViewVisitor<Node, Void> eagerVisitor = new EagerVisitor();

  private final Expr expr;

  public PutEffect(Expr expr) {
    this.expr = expr;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException {
    expr.resolve(navigator, new NodeView<N>(xml), true).visit((ViewVisitor<N, Void>) eagerVisitor);
  }

  private static final class EagerVisitor extends AbstractViewVisitor<Node, Void> {

    @Override
    @SuppressWarnings({"StatementWithEmptyBody", "UnusedVariable"})
    public Void visit(IterableNodeView<Node> nodeSet) throws XmlBuilderException {
      for (NodeView<Node> ignored : nodeSet) {
        // eagerly consume resolved iterable
      }
      return null;
    }

    @Override
    protected Void returnDefault(View<Node> view) {
      return null;
    }
  }
}
