package com.github.simy4.xpath
package scala.xpath

import expr.Expr
import javax.xml.xpath.XPathExpressionException
import parser.XPathParser

import reflect.macros.blackbox

final class XPathLiteral(val sc: StringContext) extends AnyVal {
  def xpath(args: Any*): Expr =
    macro XPathLiteral.xpathImpl
}

object XPathLiteral {
  def xpathImpl(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[Expr] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, List(lit @ Literal(Constant(str: String)))))) =>
        try {
          val _ = new XPathParser(null).parse(str)
          reify(new XPathParser(null).parse(c.Expr[String](lit).splice))
        } catch {
          case xpee: XPathExpressionException =>
            c.abort(c.enclosingPosition, s"Illegal XPath expression: ${xpee.getMessage}")
        }
      case _ =>
        c.abort(c.enclosingPosition, "xpath can only be used on string literals")
    }
  }
}

trait ToXPathLiteral {
  implicit def toXPathLiteral(sc: StringContext): XPathLiteral = new XPathLiteral(sc)
}
