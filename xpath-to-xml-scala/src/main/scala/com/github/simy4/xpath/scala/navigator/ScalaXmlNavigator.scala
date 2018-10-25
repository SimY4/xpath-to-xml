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
      Attribute(newAttr, e)
    case _          =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case e @ Element(node, _, _) =>
      val newElem = Elem(Some(element.getPrefix).filter(_.nonEmpty).orNull, element.getLocalPart, Null, node.scope, minimizeEmpty = true)
      val children = node.child
      val idx = children.size
      e.node = node.copy(child = children :+ newElem)
      Element(newElem, idx, e)
    case _                       =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case e @ Element(elem, _, _)                                                =>
      e.node = elem.copy(child = elem.child.filterNot(_.isInstanceOf[Text]) :+ Text(text))
    case a @ Attribute(attribute: XmlAttribute, parent) if attribute.isPrefixed =>
      val newAttr = XmlAttribute(Some(attribute.pre), attribute.key, Text(text), Null)
      parent.node = parent.node % newAttr
      a.meta = newAttr
    case a @ Attribute(attribute, parent)                                       =>
      val newAttr = XmlAttribute(None, attribute.key, Text(text), Null)
      parent.node = parent.node % newAttr
      a.meta = newAttr
    case _                                                                      =>
      throw new XmlBuilderException(s"Unable to set text to $node")
  }
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case e @ Element(toCopy, idx, parent: Element) =>
      val copy = toCopy.copy()
      parent.node = parent.node.copy(child = parent.node.child patch (idx, Seq(copy, toCopy), 1))
      e.index += 1
    case _                                         =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  override def remove(node: ScalaXmlNode): Unit = node match {
    case Element(_, idx, parent)                                          =>
      parent.node = parent.node.copy(child = parent.node.child patch (idx, Nil, 1))
    case Attribute(toDelete: XmlAttribute, parent) if toDelete.isPrefixed =>
      parent.node = parent.node.copy(attributes = parent.node.attributes remove
        (toDelete.getNamespace(parent.node), parent.node, toDelete.key))
    case Attribute(toDelete, parent)                                      =>
      parent.node = parent.node.copy(attributes = parent.node.attributes remove toDelete.key)
    case _                                                                =>
      throw new XmlBuilderException(s"Unable to delete node $node")
  }
}
