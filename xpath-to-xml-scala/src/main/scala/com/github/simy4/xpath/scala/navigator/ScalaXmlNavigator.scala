package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Navigator

import _root_.scala.xml.{ Elem, Null, Text, Attribute => XmlAttribute }

class ScalaXmlNavigator(xml: Root) extends Navigator[ScalaXmlNode] {
  import _root_.scala.collection.JavaConverters._

  override val root: ScalaXmlNode = xml
  override def parentOf(node: ScalaXmlNode): ScalaXmlNode = node.parent
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = parent.elements.asJava
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = parent.attributes.asJava
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = parent match {
    case e: Element =>
      val newAttr = XmlAttribute(Some(attribute.getPrefix).filter(_.nonEmpty), attribute.getLocalPart, Text(""), Null)
      e.node = e.node % newAttr
      new Attribute(newAttr, e)
    case _          =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case e: Element =>
      val node = e.node
      val children = node.child
      val idx = children.size
      val newElem = Elem(Some(element.getPrefix).filter(_.nonEmpty).orNull, element.getLocalPart, Null, node.scope,
        minimizeEmpty = true)
      e.node = node.copy(child = children :+ newElem)
      new Element(newElem, idx, e)
    case _          =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case e: Element   =>
      val elem = e.node
      e.node = elem.copy(child = elem.child.filterNot(_.isInstanceOf[Text]) :+ Text(text))
    case a: Attribute =>
      val parentNode = a.parent.node
      val newAttr = a.meta match {
        case attr: XmlAttribute if attr.isPrefixed => XmlAttribute(Some(attr.pre), attr.key, Text(text), Null)
        case attr                                  => XmlAttribute(None, attr.key, Text(text), Null)
      }
      a.parent.node = parentNode % newAttr
      a.meta = newAttr
    case _            =>
      throw new XmlBuilderException(s"Unable to set text to $node")
  }
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case e: Element =>
      val toCopy = e.node
      val copy = toCopy.copy()
      val idx = e.index
      val parentNode = e.parent.node
      e.parent.node = parentNode.copy(child = parentNode.child patch (idx, Seq(copy, toCopy), 1))
      e.index += 1
    case _                                         =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  override def remove(node: ScalaXmlNode): Unit = node match {
    case e: Element   =>
      val idx = e.index
      val parentNode = e.parent.node
      e.parent.node = parentNode.copy(child = parentNode.child patch (idx, Nil, 1))
    case a: Attribute =>
      val toDelete = a.meta
      val parentNode = a.parent.node
      val newAttr = a.meta match {
        case attr: XmlAttribute if attr.isPrefixed => parentNode.attributes remove
          (toDelete.getNamespace(parentNode), parentNode, toDelete.key)
        case _                                     => parentNode.attributes remove toDelete.key
      }
      a.parent.node = parentNode.copy(attributes = newAttr)
    case _            =>
      throw new XmlBuilderException(s"Unable to delete node $node")
  }
}
