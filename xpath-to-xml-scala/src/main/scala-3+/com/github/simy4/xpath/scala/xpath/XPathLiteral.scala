package com.github.simy4.xpath.scala.xpath

import com.github.simy4.xpath.expr.{ Expr => JExpr }
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import scala.quoted.{ Consts, Expr, Quotes, report }

object XPathLiteral {
  def xpathImpl(sc: Expr[StringContext])(using Quotes): Expr[JExpr] = {
    sc match {
      case Expr.StringContext(Consts(args)) if args.size == 1 =>
        try {
          val const = args.head
          val _ = new XPathParser(null).parse(const)
          '{new XPathParser(null).parse(${Expr(const)})}
        } catch {
          case xpee: XPathExpressionException =>
            report.throwError(s"Illegal XPath expression: ${xpee.getMessage}", sc)
        }
      case _ => report.throwError("xpath can only be used on string literals", sc)
    }
  }
}
