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
package com.github.simy4.xpath.spi;

import com.github.simy4.xpath.XmlBuilderException;

/**
 * Navigator extension SPI.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public interface NavigatorSpi {

  /**
   * Checks whether this SPI can navigate models of given type.
   *
   * @param o XML model to check
   * @return {@code true} if SPI can handle this model or {@code false} otherwise
   */
  boolean canHandle(Object o);

  /**
   * Applies expression modifications to a given XML model.
   *
   * @param xml XML model to modify
   * @param effects effects to apply
   * @param <T> XML model type
   * @return modified XML model
   * @throws XmlBuilderException if error occur during XML model modification
   */
  <T> T process(T xml, Iterable<Effect> effects) throws XmlBuilderException;
}
