package com.github.simy4.xpath
package scala.spi

import scala.navigator.{ Root, ScalaXmlNavigator }
import spi.{ Effect, NavigatorSpi }

import _root_.scala.xml.Elem

/**
  * Scala XML model navigator extension SPI.
  */
class ScalaXmlNavigatorSpi extends NavigatorSpi {
  import _root_.scala.collection.JavaConverters._

  override def canHandle(o: Any): Boolean = o.isInstanceOf[Elem]

  @throws[XmlBuilderException]("If unable process XML node")
  override def process[T](xml: T, effects: java.lang.Iterable[Effect]): T = xml match {
    case elem: Elem =>
      val root = new Root(elem)
      val navigator = new ScalaXmlNavigator(root)
      effects.asScala foreach (_.perform(navigator, root))
      root.node.asInstanceOf[T]
    case _          => throw new IllegalArgumentException("XML model is not supported")
  }
}
