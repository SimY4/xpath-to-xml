package com.github.simy4.xpath
package scala

import com.github.simy4.xpath.spi.{ Effect => JEffect }
import effects.{ PutEffect, PutValueEffect, RemoveEffect }
import expr.Expr
import javax.xml.namespace.NamespaceContext
import javax.xml.xpath.XPathExpressionException
import parser.XPathParser

abstract case class Effect(effect: JEffect)
object Effect {
  def put(expr: Expr): Effect =
    new Effect(new PutEffect(expr)) {}

  def put(xpath: String)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(put(new XPathParser(ns).parse(xpath)))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }

  def putValue(expr: Expr, value: Any): Effect =
    new Effect(new PutValueEffect(expr, value)) {}

  def putValue(xpath: String, value: Any)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(putValue(new XPathParser(ns).parse(xpath), value))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }

  def remove(expr: Expr): Effect =
    new Effect(new RemoveEffect(expr)) {}

  def remove(xpath: String)(implicit ns: NamespaceContext): Either[XPathExpressionException, Effect] =
    try Right(remove(new XPathParser(ns).parse(xpath)))
    catch {
      case xpee: XPathExpressionException => Left(xpee)
    }
}
