package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Navigator

import _root_.scala.xml.{Elem, Null, Text, Attribute => XmlAttribute}

class ScalaXmlNavigator(override val root: Root) extends Navigator[ScalaXmlNode] {
  import scala.compat.Converters._

  override def parentOf(node: ScalaXmlNode): ScalaXmlNode = node.parent
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = parent.elements
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = parent.attributes
  @throws[XmlBuilderException]("If unable to create attribute for given node")
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = parent match {
    case e: Element =>
      Attribute(XmlAttribute(attribute.getPrefix match {
        case pre if pre.nonEmpty => Some(pre)
        case _                   => None
      }, attribute.getLocalPart, Text(""), Null), e)
    case _ =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  @throws[XmlBuilderException]("If unable to create element for given node")
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case e: Element =>
      Element(Elem(element.getPrefix match {
        case pre if pre.nonEmpty => pre
        case _                   => null
      }, element.getLocalPart, Null, e.node.scope, minimizeEmpty = true), e)
    case _ =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  @throws[XmlBuilderException]("If unable to set text to given node")
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case e: Element =>
      val elem = e.node
      e.node = elem.copy(child = elem.child.filterNot(_.isInstanceOf[Text]) :+ Text(text))
    case a: Attribute =>
      val attr = a.meta
      val newAttr = XmlAttribute(Some(attr).collect {
        case a: XmlAttribute if attr.isPrefixed => a.pre
      }, attr.key, Text(text), Null)
      a.meta = newAttr
    case _ =>
      throw new XmlBuilderException(s"Unable to set text to $node")
  }
  @throws[XmlBuilderException]("If unable to prepend copy to given node")
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case e: Element =>
      val toCopy = e.node
      val copy = toCopy.copy()
      val idx = e.index
      val parentNode = e.parent.node
      e.parent.node = parentNode.copy(child = parentNode.child.patch(idx, Seq(copy, toCopy), 1))
      e.index += 1
    case _ =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  @throws[XmlBuilderException]("If unable to remove given node")
  override def remove(node: ScalaXmlNode): Unit = node match {
    case e: Element =>
      val idx = e.index
      val parentNode = e.parent.node
      e.parent.node = parentNode.copy(child = parentNode.child.patch(idx, Nil, 1))
    case a: Attribute =>
      val toDelete = a.meta
      val parentNode = a.parent.node
      val newAttr = a.meta match {
        case attr: XmlAttribute if attr.isPrefixed =>
          parentNode.attributes.remove(toDelete.getNamespace(parentNode), parentNode, toDelete.key)
        case _ => parentNode.attributes.remove(toDelete.key)
      }
      a.parent.node = parentNode.copy(attributes = newAttr)
    case _ =>
      throw new XmlBuilderException(s"Unable to remove node $node")
  }
}
