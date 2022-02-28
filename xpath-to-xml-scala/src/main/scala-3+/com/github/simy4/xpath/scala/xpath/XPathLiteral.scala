/*
 * Copyright 2022 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath.scala.xpath

import com.github.simy4.xpath.expr.Expr as JExpr
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import scala.quoted.{ Exprs, Expr, Quotes, Varargs, quotes }

object XPathLiteral:
  def xpathImpl(sc: Expr[StringContext])(using Quotes): Expr[JExpr] =
    import quotes.reflect.report
    sc match
      case '{StringContext(${Varargs(Exprs(args))}: _*)} if args.size == 1 =>
        try {
          val const = args.head
          val _ = new XPathParser(null).parse(const)
          '{new XPathParser(null).parse(${Expr(const)})}
        } catch {
          case xpee: XPathExpressionException =>
            report.errorAndAbort(s"Illegal XPath expression: ${xpee.getMessage}", sc)
        }
      case _ => report.errorAndAbort("xpath can only be used on string literals", sc)
