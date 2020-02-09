package com.github.simy4.xpath
package scala

import com.github.simy4.xpath.spi.{ Effect => JEffect }
import effects.{ PutEffect, PutValueEffect, RemoveEffect }
import expr.Expr
import javax.xml.namespace.NamespaceContext
import javax.xml.xpath.XPathExpressionException
import parser.XPathParser

/**
 * XML model modification effect.
 *
 * @see com.github.simy4.xpath.spi.Effect
 * @author Alex Simkin
 * @since 2.2
 */
sealed abstract case class Effect private (effect: JEffect)
object Effect {

  /**
   * Creates XML put effect.
   *
   * @param expr xpath expression
   * @return XML put effect
   */
  def put(expr: Expr): Effect =
    new Effect(new PutEffect(expr)) {}

  /**
   * Creates XML put effect.
   *
   * @param xpath xpath string
   * @return XML put effect
   */
  def put(xpath: String)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(put(new XPathParser(ns).parse(xpath)))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }

  /**
   * Creates XML put value effect.
   *
   * @param expr xpath expression
   * @return XML put value effect
   */
  def putValue(expr: Expr, value: Any): Effect =
    new Effect(new PutValueEffect(expr, value)) {}

  /**
   * Creates XML put value effect.
   *
   * @param xpath xpath string
   * @return XML put value effect
   */
  def putValue(xpath: String, value: Any)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(putValue(new XPathParser(ns).parse(xpath), value))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }

  /**
   * Creates XML remove effect.
   *
   * @param expr xpath expression
   * @return XML remove effect
   */
  def remove(expr: Expr): Effect =
    new Effect(new RemoveEffect(expr)) {}

  /**
   * Creates XML remove effect.
   *
   * @param xpath xpath string
   * @return XML remove effect
   */
  def remove(xpath: String)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(remove(new XPathParser(ns).parse(xpath)))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }
}
