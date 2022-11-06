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
package com.github.simy4.xpath
package scala.spi

import spi.{ Effect, NavigatorSpi }
import xml.Elem

import scala.navigator.{ Root, ScalaXmlNavigator }

/**
 * Scala XML model navigator extension SPI.
 */
class ScalaXmlNavigatorSpi extends NavigatorSpi {
  @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
  override def canHandle(o: Any): Boolean = o.isInstanceOf[Elem]

  @throws[XmlBuilderException]("If unable process XML node")
  @SuppressWarnings(Array("org.wartremover.warts.AsInstanceOf", "org.wartremover.warts.Throw"))
  override def process[T](xml: T, effects: java.lang.Iterable[Effect]): T =
    xml match {
      case elem: Elem =>
        val root            = new Root(elem)
        val navigator       = new ScalaXmlNavigator(root)
        val effectsIterator = effects.iterator
        while (effectsIterator.hasNext) effectsIterator.next.perform(navigator, root)
        root.node.asInstanceOf[T]
      case _ => throw new IllegalArgumentException("XML model is not supported")
    }
}
