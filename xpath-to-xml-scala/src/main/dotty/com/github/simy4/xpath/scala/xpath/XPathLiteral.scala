package com.github.simy4.xpath.scala.xpath

import com.github.simy4.xpath.expr.{ Expr => JExpr }
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import scala.quoted._

final class XPathLiteral(private val sc: StringContext) extends AnyVal with
  inline def xpath(args: Any*): JExpr = ${XPathLiteral.xpathImpl('this)}

object XPathLiteral with
  def xpathImpl(sc: Expr[XPathLiteral])(given qctx: QuoteContext): Expr[JExpr] =
    import qctx.tasty.{_, given}

    sc.unseal.underlyingArgument match
      case Apply(_, List(Apply(_, List(Typed(Repeated(List(lit @ Literal(Constant(str: String))), _), _))))) =>
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
