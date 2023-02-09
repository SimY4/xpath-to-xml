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

/**
 * XML elements view.
 *
 * @param <N> XML node type
 * @author Alex Simkin
 * @since 1.0
 */
public interface View<N extends Node> extends Comparable<View<N>> {

  /**
   * Compare views by XQuery comparison rules.
   *
   * @param view view to compare with
   * @return comparison result
   */
  @Override
  int compareTo(View<N> view);

  /**
   * Converts this view to a boolean value.
   *
   * @return boolean value
   */
  boolean toBoolean();

  /**
   * Converts this view to a numeric value.
   *
   * @return numeric value
   */
  double toNumber();

  /**
   * Converts this view to a string value.
   *
   * @return string value
   */
  @Override
  String toString();

  /**
   * Visits current XML element.
   *
   * @param visitor XML element visitor
   * @param <T> type of return value
   * @return visitor result
   * @throws XmlBuilderException if error occur during XML model modification
   */
  <T> T visit(ViewVisitor<N, T> visitor) throws XmlBuilderException;
}
