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
package com.github.simy4.xpath.spi;

import com.github.simy4.xpath.XmlBuilderException;
import com.github.simy4.xpath.navigator.Navigator;
import com.github.simy4.xpath.navigator.Node;

/**
 * XML model modification effect.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@FunctionalInterface
public interface Effect {

  /**
   * Performs effect on a particular xml model.
   *
   * @param xml XML model to modify
   * @param navigator XML model navigator
   * @param <N> XML model type
   * @throws XmlBuilderException if error occur during XML model modification
   */
  <N extends Node> void perform(Navigator<N> navigator, N xml) throws XmlBuilderException;
}
