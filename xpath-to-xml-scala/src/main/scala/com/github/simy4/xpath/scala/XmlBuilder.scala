package com.github.simy4.xpath
package scala

import spi.ScalaXmlNavigatorSpi

import xml.Elem

object XmlBuilder {
  import compat.Converters._

  def apply(effects: Effect*): BuilderPartiallyApplied = new BuilderPartiallyApplied(effects)

  def apply(effects: Iterable[Effect]): BuilderPartiallyApplied = new BuilderPartiallyApplied(effects)

  final class BuilderPartiallyApplied private[XmlBuilder] (private val effects: Iterable[Effect]) extends AnyVal {
    def apply(xml: Elem): Either[XmlBuilderException, Elem] =
      try Right(new ScalaXmlNavigatorSpi().process(xml, effects.map(_.effect)))
      catch {
        case xbe: XmlBuilderException => Left(xbe)
      }
  }
}
