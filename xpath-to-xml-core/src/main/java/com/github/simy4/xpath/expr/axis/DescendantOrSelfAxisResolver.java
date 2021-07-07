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
package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.ReadOnlyIterator;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

public class DescendantOrSelfAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  private final boolean self;

  public DescendantOrSelfAxisResolver(QName name, boolean self) {
    super(name);
    this.self = self;
  }

  @Override
  protected <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
    return new DescendantOrSelfIterable<N>(navigator, view.getNode(), self);
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> view, int position) throws XmlBuilderException {
    if (isWildcard()) {
      throw new XmlBuilderException("Wildcard elements cannot be created");
    }
    return new NodeView<N>(navigator.createElement(view.getNode(), name), position);
  }

  @Override
  public String toString() {
    return (self ? "descendant-or-self::" : "descendant::") + super.toString();
  }

  private static final class DescendantOrSelfIterable<T extends Node> implements Iterable<T> {

    private final Navigator<T> navigator;
    private final T node;
    private final boolean self;

    private DescendantOrSelfIterable(Navigator<T> navigator, T node, boolean self) {
      this.navigator = navigator;
      this.node = node;
      this.self = self;
    }

    @Override
    public Iterator<T> iterator() {
      final Iterator<T> descendantOrSelf =
          new DescendantOrSelf<T>(navigator, Collections.singleton(node));
      if (!self) {
        descendantOrSelf.next();
      }
      return descendantOrSelf;
    }
  }

  static final class DescendantOrSelf<T extends Node> extends ReadOnlyIterator<T> {

    private final Navigator<T> navigator;
    private final Queue<Iterable<? extends T>> stack = new ArrayDeque<Iterable<? extends T>>();
    private Iterator<? extends T> current;

    private DescendantOrSelf(Navigator<T> navigator, Iterable<? extends T> current) {
      this(navigator, current.iterator());
    }

    DescendantOrSelf(Navigator<T> navigator, Iterator<? extends T> current) {
      this.navigator = navigator;
      this.current = current;
    }

    @Override
    public boolean hasNext() {
      boolean currentHasNext;
      while (!(currentHasNext = current.hasNext()) && !stack.isEmpty()) {
        current = stack.poll().iterator();
      }
      return currentHasNext;
    }

    @Override
    public T next() {
      final T next = current.next();
      stack.offer(navigator.elementsOf(next));
      return next;
    }
  }
}
