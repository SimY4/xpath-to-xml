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
import com.github.simy4.xpath.view.NodeView;
import com.github.simy4.xpath.view.View;

/**
 * XPath expression model. Every XPath expression is also a XPath predicate.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface Expr {

  /**
   * Evaluate this expression using given context.
   *
   * @param navigator XML navigator
   * @param view XML node view
   * @param greedy whether resolution is greedy
   * @param <N> XML model type
   * @return evaluated XML view
   * @throws XmlBuilderException if error occur during XML model modification
   */
  <N extends Node> View<N> resolve(Navigator<N> navigator, NodeView<N> view, boolean greedy)
      throws XmlBuilderException;
}
