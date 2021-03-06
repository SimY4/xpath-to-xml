package com.github.simy4.xpath
package scala.spi

import scala.navigator.{ Root, ScalaXmlNavigator }
import spi.{ Effect, NavigatorSpi }

import xml.Elem

/**
 * Scala XML model navigator extension SPI.
 */
class ScalaXmlNavigatorSpi extends NavigatorSpi {
  override def canHandle(o: Any): Boolean = o.isInstanceOf[Elem]

  @throws[XmlBuilderException]("If unable process XML node")
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
