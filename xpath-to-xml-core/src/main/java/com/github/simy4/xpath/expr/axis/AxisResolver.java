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
import com.github.simy4.xpath.view.IterableNodeView;
import com.github.simy4.xpath.view.NodeView;

/**
 * XPath step axis resolver.
 *
 * @author Alex Simkin
 * @since 1.2
 */
public interface AxisResolver {

  /**
   * Traverses XML nodes for the nodes that matches this axis.
   *
   * @param navigator XML navigator
   * @param parent parent XML node view
   * @param greedy whether resolution is greedy
   * @param <N> XML node type
   * @return ordered set of matching nodes
   * @throws XmlBuilderException if error occur during XML node creation
   */
  <N extends Node> IterableNodeView<N> resolveAxis(
      Navigator<N> navigator, NodeView<N> parent, boolean greedy) throws XmlBuilderException;

  /**
   * Creates new node of this axis type.
   *
   * @param navigator XML navigator
   * @param parent parent XML node view
   * @param position new XML node position
   * @param <N> XML node type
   * @return newly created node
   * @throws XmlBuilderException if error occur during XML node creation
   */
  <N extends Node> NodeView<N> createAxisNode(
      Navigator<N> navigator, NodeView<N> parent, int position) throws XmlBuilderException;
}
