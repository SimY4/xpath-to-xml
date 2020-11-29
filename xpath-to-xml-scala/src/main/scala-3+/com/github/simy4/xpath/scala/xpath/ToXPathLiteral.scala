package com.github.simy4.xpath
package scala.xpath

import expr.Expr

trait ToXPathLiteral {
  extension (inline sc: StringContext) inline def xpath(inline args: Any*): Expr = ${XPathLiteral.xpathImpl('sc)}
}
