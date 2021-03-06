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
