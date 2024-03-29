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
package com.github.simy4.xpath.view;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Function;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;

public final class NodeView<N extends Node> implements IterableNodeView<N>, Serializable {

  private static final long serialVersionUID = 1L;

  private final N node;
  private final int position;
  private final boolean hasNext;
  private final boolean isNew;
  private boolean marked;

  public NodeView(N node) {
    this(node, 1, false);
  }

  public NodeView(N node, int position) {
    this(node, position, false, true, false);
  }

  public NodeView(N node, int position, boolean hasNext) {
    this(node, position, hasNext, false, false);
  }

  private NodeView(N node, int position, boolean hasNext, boolean isNew, boolean marked) {
    this.node = node;
    this.position = position;
    this.hasNext = hasNext;
    this.isNew = isNew;
    this.marked = marked;
  }

  @Override
  public int compareTo(View<N> other) {
    return toString().compareTo(other.toString());
  }

  @Override
  public boolean toBoolean() {
    return true;
  }

  @Override
  public double toNumber() {
    try {
      return Double.parseDouble(node.getText());
    } catch (NumberFormatException nfe) {
      return Double.NaN;
    }
  }

  @Override
  public String toString() {
    return node.getText();
  }

  @Override
  public <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException {
    return visitor.visit(this);
  }

  @Override
  public IterableNodeView<N> flatMap(
      Function<? super NodeView<N>, ? extends IterableNodeView<N>> fmap) {
    return fmap.apply(copy(1, false));
  }

  @Override
  public Iterator<NodeView<N>> iterator() {
    return Collections.singleton(copy(1, false)).iterator();
  }

  public N getNode() {
    return node;
  }

  public int getPosition() {
    return position;
  }

  public boolean hasNext() {
    return hasNext;
  }

  public boolean isNew() {
    return isNew;
  }

  public boolean isMarked() {
    return marked;
  }

  public void mark() {
    marked = true;
  }

  NodeView<N> copy(int position, boolean hasNext) {
    return this.position == position && this.hasNext == hasNext
        ? this
        : new NodeView<N>(node, position, hasNext, isNew, marked);
  }
}
