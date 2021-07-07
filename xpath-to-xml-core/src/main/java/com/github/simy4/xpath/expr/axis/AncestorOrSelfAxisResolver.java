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
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

import java.util.Iterator;

public class AncestorOrSelfAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  private final boolean self;

  public AncestorOrSelfAxisResolver(QName name, boolean self) {
    super(name);
    this.self = self;
  }

  @Override
  protected <N extends Node> Iterable<? extends N> traverseAxis(
      Navigator<N> navigator, NodeView<N> parent) {
    return () -> new AncestorOrSelf<>(navigator, parent.getNode(), self);
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> parent, int position) throws XmlBuilderException {
    if (self) {
      throw new XmlBuilderException("Ancestor-of-self axis cannot modify XML model");
    } else {
      throw new XmlBuilderException("Ancestor axis cannot modify XML model");
    }
  }

  @Override
  public String toString() {
    return (self ? "ancestor-or-self::" : "ancestor::") + super.toString();
  }

  private static final class AncestorOrSelf<T extends Node> implements Iterator<T> {

    private final Navigator<T> navigator;
    private T current;

    private AncestorOrSelf(Navigator<T> navigator, T current, boolean self) {
      this.navigator = navigator;
      this.current = self ? current : navigator.parentOf(current);
    }

    @Override
    public boolean hasNext() {
      return null != current;
    }

    @Override
    public T next() {
      final T next = current;
      current = navigator.parentOf(next);
      return next;
    }
  }
}
