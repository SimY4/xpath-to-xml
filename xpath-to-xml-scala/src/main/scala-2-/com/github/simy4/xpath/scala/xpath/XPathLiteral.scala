/*
 * Copyright 2021 Alex Simkin
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

import com.github.simy4.xpath.expr.Expr
import com.github.simy4.xpath.parser.XPathParser
import javax.xml.xpath.XPathExpressionException

import reflect.macros.blackbox

final class XPathLiteral(private val sc: StringContext) extends AnyVal {
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
