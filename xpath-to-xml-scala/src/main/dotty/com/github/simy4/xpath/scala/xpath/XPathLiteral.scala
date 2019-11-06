package com.github.simy4.xpath.scala.xpath

import com.github.simy4.xpath.expr.{ Expr => JExpr }
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import scala.quoted._

final class XPathLiteral(val sc: StringContext) extends AnyVal
  inline def xpath(args: Any*): JExpr = ${XPathLiteral.xpathImpl('sc)}

object XPathLiteral
  def xpathImpl(sc: Expr[StringContext])(given qctx: QuoteContext): Expr[JExpr] =
    import qctx.tasty.{_, given}
    sc.unseal match
      case Apply(_, List(Apply(_, List(lit @ Literal(Constant(str: String)))))) =>
        try
          val _ = new XPathParser(null).parse(str)
          '{new XPathParser(null).parse(${lit.seal.cast[String]})}
        catch
          case xpee: XPathExpressionException =>
            qctx.error(s"Illegal XPath expression: ${xpee.getMessage}")
            '{null}
      case _ =>
        qctx.error("xpath can only be used on string literals")
        '{null}
