package com.github.simy4.xpath
package scala.syntax

import javax.xml.namespace.NamespaceContext

import _root_.scala.collection.{ Iterable, Map }
import _root_.scala.xml.Elem

object elem extends ToXmlElemOps

trait ToXmlElemOps {
  implicit def toXmlElemOps(elem: Elem): XmlElemOps = new XmlElemOps(elem)
}

final class XmlElemOps(private val elem: Elem) extends AnyVal {
  def put(xpath: String)(implicit namespaceContext: NamespaceContext = null): Elem = new XmlBuilder(namespaceContext)
    .put(xpath)
    .build(elem)
  def putValue(xpath: String, value: Any)(implicit namespaceContext: NamespaceContext = null): Elem =
    new XmlBuilder(namespaceContext)
      .put(xpath, value)
      .build(elem)
  def putAll(xpaths: Iterable[String])(implicit namespaceContext: NamespaceContext = null): Elem =
    (new XmlBuilder(namespaceContext) /: xpaths) { _.put(_) } build elem
  def putAllValues(xpathToValueMap: Map[String, AnyRef])(implicit namespaceContext: NamespaceContext = null): Elem =
    (new XmlBuilder(namespaceContext) /: xpathToValueMap) {
      (builder, entry) => builder.put(entry._1, entry._2)
    } build elem
  def remove(xpath: String)(implicit namespaceContext: NamespaceContext = null): Elem = new XmlBuilder(namespaceContext)
    .remove(xpath)
    .build(elem)
  def removeAll(xpaths: Iterable[String])(implicit namespaceContext: NamespaceContext = null): Elem =
    (new XmlBuilder(namespaceContext) /: xpaths) { _.remove(_) } build elem
}