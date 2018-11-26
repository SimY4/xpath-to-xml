package com.github.simy4.xpath
package scala
package impl

import javax.xml.namespace.NamespaceContext

import _root_.scala.collection.{ Iterable, Map }
import _root_.scala.xml.Elem

trait ToXmlElemOps {
  implicit def toXmlElemOps(elem: Elem)(implicit namespaceContext: NamespaceContext = null): XmlElemOps =
    new XmlElemOps(elem, namespaceContext)
}

final class XmlElemOps(private val elem: Elem, private val namespaceContext: NamespaceContext) {
  def put(xpath: String): Elem = new XmlBuilder(namespaceContext)
    .put(xpath)
    .build(elem)
  def put(xpath: String, value: Any): Elem = new XmlBuilder(namespaceContext)
    .put(xpath, value)
    .build(elem)
  def putAll(xpaths: String*): Elem = new XmlBuilder(namespaceContext)
    .putAll(xpaths: _*)
    .build(elem)
  def putAll(xpaths: Iterable[String]): Elem =
    xpaths.foldLeft(new XmlBuilder(namespaceContext)) { _ put _ } build elem
  def putAll(xpathToValueMap: Map[String, AnyRef]): Elem =
    xpathToValueMap.foldLeft(new XmlBuilder(namespaceContext)) { (builder, entry) =>
      builder.put(entry._1, entry._2)
    } build elem
  def remove(xpath: String): Elem = new XmlBuilder(namespaceContext)
    .remove(xpath)
    .build(elem)
  def removeAll(xpaths: String*): Elem = new XmlBuilder(namespaceContext)
    .removeAll(xpaths: _*)
    .build(elem)
  def removeAll(xpaths: Iterable[String]): Elem =
    xpaths.foldLeft(new XmlBuilder(namespaceContext)) { _ remove _ } build elem
}
