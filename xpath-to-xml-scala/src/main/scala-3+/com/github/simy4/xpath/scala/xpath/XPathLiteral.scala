package com.github.simy4.xpath.scala.xpath

import com.github.simy4.xpath.expr.{ Expr => JExpr }
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import scala.quoted.{ Expr, QuoteContext, report }

final class XPathLiteral(private val sc: StringContext) extends AnyVal {
  inline def xpath(args: Any*): JExpr = ${XPathLiteral.xpathImpl('this)}
}

object XPathLiteral {
  def xpathImpl(sc: Expr[XPathLiteral])(using qctx: QuoteContext): Expr[JExpr] = {
    import qctx.reflect._

    sc.unseal.underlyingArgument match {
      case Apply(_, List(Apply(_, List(Typed(Repeated(List(lit @ Literal(const)), _), _))))) =>
        try {
          val _ = new XPathParser(null).parse(const.value.asInstanceOf[String])
          '{new XPathParser(null).parse(${lit.seal.cast[String]})}
        } catch {
          case xpee: XPathExpressionException =>
            report.throwError(s"Illegal XPath expression: ${xpee.getMessage}", sc)
        }
      case _ => report.throwError("xpath can only be used on string literals", sc)
    }
  }
}
