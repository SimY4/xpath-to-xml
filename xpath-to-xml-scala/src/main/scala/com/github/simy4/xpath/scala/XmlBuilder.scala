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
package com.github.simy4.xpath
package scala

import spi.ScalaXmlNavigatorSpi

import xml.Elem

/**
 * XML model modifier that works via XPath expressions processing.
 *
 * @see com.github.simy4.xpath.XmlBuilder
 * @author Alex Simkin
 * @since 2.2
 */
object XmlBuilder {
  import compat.Converters._

  def apply(effects: Effect*) = new BuilderPartiallyApplied(effects)

  def apply(effects: Iterable[Effect]) = new BuilderPartiallyApplied(effects)

  final class BuilderPartiallyApplied private[XmlBuilder] (private val effects: Iterable[Effect]) extends AnyVal {
    def apply(xml: Elem): Either[XmlBuilderException, Elem] =
      try Right(new ScalaXmlNavigatorSpi().process(xml, effects.map(_.effect)))
      catch {
        case xbe: XmlBuilderException => Left(xbe)
      }
  }
}
