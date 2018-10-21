package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Navigator

import _root_.scala.xml.{ Elem, Null, Text, Attribute => XmlAttribute }

class ScalaXmlNavigator(xml: ScalaXmlNode.Root) extends Navigator[ScalaXmlNode] {
  import ScalaXmlNode._
  import _root_.scala.collection.JavaConverters._

  override val root: ScalaXmlNode = xml
  override def parentOf(node: ScalaXmlNode): ScalaXmlNode = node match {
    case Root(_)              => null
    case Element(_, parent)   => parent
    case Attribute(_, parent) => parent
  }
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case parent @ Root(elem)       => Seq(Element(elem, parent))
    case parent @ Element(elem, _) => for {
      child <- elem \ "_" if child.isInstanceOf[Elem]
    } yield Element(child.asInstanceOf[Elem], parent)
    case _                         => Nil
  }).asJava
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case parent @ Element(elem, _) => for {
      attr <- elem.attributes
    } yield Attribute(attr, parent)
    case _                         => Nil
  }).asJava
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = parent match {
    case p: Parent =>
      val newAttr = XmlAttribute(Some(attribute.getPrefix).filter(_.nonEmpty), attribute.getLocalPart, Text(""), Null)
      p transform { _ % newAttr }
      Attribute(newAttr, p)
    case _         =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case p: Parent =>
      val newElem = Elem(Some(element.getPrefix).filter(_.nonEmpty).orNull, element.getLocalPart, Null, p.node.scope, minimizeEmpty = true)
      p transform { elem => elem.copy(child = elem.child ++ newElem) }
      Element(newElem, p)
    case _         =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case parent: Parent                                                  =>
      parent transform { elem => elem.copy(child = elem.child.filterNot(_.isInstanceOf[Text]) :+ Text(text)) }
    case Attribute(attribute: XmlAttribute, parent) if attribute.isPrefixed =>
      val newAttr = XmlAttribute(Some(attribute.pre), attribute.key, Text(text), Null)
      parent transform { _ % newAttr }
    case Attribute(attribute, parent)                                    =>
      val newAttr = XmlAttribute(None, attribute.key, Text(text), Null)
      parent transform { _ % newAttr }
    case _                                                               =>
      throw new XmlBuilderException(s"Unable to set text to $node")
  }
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case Element(toCopy, parent) =>
      val copy = toCopy.copy()
      parent transform { elem =>
        val children = elem.child.toList
        val idx = children indexOf elem
        val newChildren = if (0 == idx)
          copy :: children
        else
          children patch (idx - 1, Seq(copy), 1)
        elem.copy(child = newChildren)
      }
    case _                       =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  override def remove(node: ScalaXmlNode): Unit = node match {
    case Element(toDelete, parent)                                        =>
      parent transform { elem => elem.copy(child = elem.child filterNot (_ eq toDelete)) }
    case Attribute(toDelete: XmlAttribute, parent) if toDelete.isPrefixed =>
      parent transform { elem => elem.copy(attributes = elem.attributes remove
        (toDelete.getNamespace(elem), elem, toDelete.key)) }
    case Attribute(toDelete, parent)                                      =>
      parent transform { elem => elem.copy(attributes = elem.attributes remove toDelete.key) }
    case _                                                                =>
      throw new XmlBuilderException(s"Unable to delete node $node")
  }
}
