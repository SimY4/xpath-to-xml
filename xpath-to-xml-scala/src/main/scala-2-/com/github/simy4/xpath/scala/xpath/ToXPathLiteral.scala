package com.github.simy4.xpath.scala.xpath

trait ToXPathLiteral {
  implicit def toXPathLiteral(sc: StringContext): XPathLiteral = new XPathLiteral(sc)
}
