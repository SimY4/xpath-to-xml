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

import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.NumberView;

import java.io.Serializable;

public class NumberExpr implements Expr, Serializable {

  private static final long serialVersionUID = 1L;

  private final NumberView<?> number;

  public NumberExpr(double number) {
    this.number = new NumberView<Node>(number);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <N extends Node> NumberView<N> resolve(
      Navigator<N> navigator, NodeView<N> view, boolean greedy) {
    return (NumberView<N>) number;
  }

  @Override
  public String toString() {
    return number.toString();
  }
}
