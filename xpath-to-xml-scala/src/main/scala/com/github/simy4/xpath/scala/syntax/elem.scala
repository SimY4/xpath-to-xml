package com.github.simy4.xpath
package scala.syntax

import javax.xml.namespace.NamespaceContext

import collection.{ Iterable, Map }
import xml.Elem

trait ElemSyntax {
  implicit def toXmlElemOps(elem: Elem): XmlElemOps = new XmlElemOps(elem)
}

final class XmlElemOps(private val elem: Elem) extends AnyVal {
  def put(xpath: String)(implicit namespaceContext: NamespaceContext = null): Elem =
    new XmlBuilder(namespaceContext)
      .put(xpath)
      .build(elem)
  def putValue(xpath: String, value: Any)(implicit namespaceContext: NamespaceContext = null): Elem =
    new XmlBuilder(namespaceContext)
      .put(xpath, value)
      .build(elem)
  def putAll(xpaths: Iterable[String])(implicit namespaceContext: NamespaceContext = null): Elem =
    xpaths.foldLeft(new XmlBuilder(namespaceContext)) { _.put(_) }.build(elem)
  def putAllValues(xpathToValueMap: Map[String, AnyRef])(implicit namespaceContext: NamespaceContext = null): Elem =
    xpathToValueMap
      .foldLeft(new XmlBuilder(namespaceContext)) { (builder, entry) =>
        builder.put(entry._1, entry._2)
      }
      .build(elem)
  def remove(xpath: String)(implicit namespaceContext: NamespaceContext = null): Elem =
    new XmlBuilder(namespaceContext)
      .remove(xpath)
      .build(elem)
  def removeAll(xpaths: Iterable[String])(implicit namespaceContext: NamespaceContext = null): Elem =
    xpaths.foldLeft(new XmlBuilder(namespaceContext)) { _.remove(_) }.build(elem)
}
