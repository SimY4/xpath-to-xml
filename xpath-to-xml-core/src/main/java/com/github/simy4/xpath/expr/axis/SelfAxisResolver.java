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

import java.util.Collections;

public class SelfAxisResolver extends AbstractAxisResolver {

  private static final long serialVersionUID = 1L;

  public SelfAxisResolver(QName name) {
    super(name);
  }

  @Override
  protected <N extends Node> Iterable<N> traverseAxis(Navigator<N> navigator, NodeView<N> view) {
    return Collections.singletonList(view.getNode());
  }

  @Override
  public <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> view, int position) throws XmlBuilderException {
    throw new XmlBuilderException("Self axis cannot modify XML model");
  }

  @Override
  public String toString() {
    return "self::" + super.toString();
  }
}
