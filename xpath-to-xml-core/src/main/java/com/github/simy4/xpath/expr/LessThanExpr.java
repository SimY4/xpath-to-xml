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
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.BooleanView;
import com.github.simy4.xpath.view.View;

public class LessThanExpr extends AbstractOperationExpr {

  private static final long serialVersionUID = 1L;

  public LessThanExpr(Expr leftExpr, Expr rightExpr) {
    super(leftExpr, rightExpr);
  }

  @Override
  public <N extends Node> View<N> resolve(
      Navigator<N> navigator, View<N> left, View<N> right, boolean greedy)
      throws XmlBuilderException {
    final boolean lt = 0 > Double.compare(left.toNumber(), right.toNumber());
    if (!lt && greedy) {
      throw new XmlBuilderException(
          "Can not apply a 'less than' operator to: " + left + " and: " + right);
    }
    return BooleanView.of(lt);
  }

  @Override
  protected String operator() {
    return "<";
  }
}
