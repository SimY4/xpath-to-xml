/*
 * Copyright 2021 Alex Simkin
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
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

import java.io.Serializable;

abstract class AbstractOperationExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final Expr leftExpr;
  private final Expr rightExpr;

  protected AbstractOperationExpr(Expr leftExpr, Expr rightExpr) {
    this.leftExpr = leftExpr;
    this.rightExpr = rightExpr;
  }

  @Override
  public final <N extends Node> View<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) throws XmlBuilderException {
    final boolean newGreedy = !view.hasNext() && greedy;
    final View<N> leftView = leftExpr.resolve(navigator, view, newGreedy);
    final View<N> rightView = rightExpr.resolve(navigator, view, newGreedy);
    return resolve(navigator, leftView, rightView, newGreedy);
  }

  protected abstract <N extends Node> View<N> resolve(
      Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
      throws XmlBuilderException;

  protected abstract String operator();

  @Override
  public final String toString() {
    return leftExpr.toString() + operator() + rightExpr.toString();
  }
}
