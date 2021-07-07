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
package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;

import java.io.Serializable;

public final class LiteralView<N extends Node> implements View<N>, Serializable {

  private static final long serialVersionUID = 1L;

  private final String literal;

  public LiteralView(String literal) {
    this.literal = literal;
  }

  @Override
  public int compareTo(View<N> other) {
    return literal.compareTo(other.toString());
  }

  @Override
  public boolean toBoolean() {
    return !literal.isEmpty();
  }

  @Override
  public double toNumber() {
    try {
      return Double.parseDouble(literal);
    } catch (NumberFormatException nfe) {
      return Double.NaN;
    }
  }

  @Override
  public String toString() {
    return literal;
  }

  @Override
  public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
    return visitor.visit(this);
  }
}
