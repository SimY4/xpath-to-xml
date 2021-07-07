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
package com.github.simy4.xpath.expr.axis;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;
import com.github.simy4.xpath.util.Predicate;
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeSetView;
import com.github.simy4.xpath.view.NodeView;

import javax.xml.namespace.QName;

import java.io.Serializable;

abstract class AbstractAxisResolver implements AxisResolver, Predicate<Node>, Serializable {

  private static final long serialVersionUID = 1L;

  protected final QName name;

  protected AbstractAxisResolver(QName name) {
    this.name = name;
  }

  @Override
  public final <N extends Node> IterableNodeView<N> resolveAxis(
      Navigator<N> navigator, NodeView<N> parent, boolean greedy) throws XmlBuilderException {
    IterableNodeView<N> result = NodeSetView.of(traverseAxis(navigator, parent), this);
    if (greedy && !result.toBoolean()) {
      result = createAxisNode(navigator, parent, 1);
    }
    return result;
  }

  protected abstract <N extends Node> Iterable<? extends N> traverseAxis(
      Navigator<N> navigator, NodeView<N> view);

  protected final boolean isWildcard() {
    return "*".equals(name.getNamespaceURI()) || "*".equals(name.getLocalPart());
  }

  @Override
  public boolean test(Node t) {
    final QName actual = t.getName();
    return test(name.getNamespaceURI(), actual.getNamespaceURI())
        && test(name.getLocalPart(), actual.getLocalPart());
  }

  private boolean test(String expected, String actual) {
    return "*".equals(expected) || "*".equals(actual) || expected.equals(actual);
  }

  @Override
  public String toString() {
    return name.toString();
  }
}
