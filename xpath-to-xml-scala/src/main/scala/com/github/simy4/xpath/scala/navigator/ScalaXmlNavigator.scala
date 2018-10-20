package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Navigator

import _root_.scala.xml.{ Attribute, Elem, Null, Text }

class ScalaXmlNavigator(xml: ScalaXmlNode.Root) extends Navigator[ScalaXmlNode] {
  import _root_.scala.collection.JavaConverters._

  override val root: ScalaXmlNode = xml
  override def parentOf(node: ScalaXmlNode): ScalaXmlNode = node match {
    case ScalaXmlNode.Root(_)              => null
    case ScalaXmlNode.Element(_, parent)   => parent
    case ScalaXmlNode.Attribute(_, parent) => parent
  }
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case parent @ ScalaXmlNode.Root(elem)       => Seq(ScalaXmlNode.Element(elem, parent))
    case parent @ ScalaXmlNode.Element(elem, _) => for {
      child <- elem \ "_" if child.isInstanceOf[Elem]
    } yield ScalaXmlNode.Element(child.asInstanceOf[Elem], parent)
    case ScalaXmlNode.Attribute(_, _)           => Nil
  }).asJava
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case parent @ ScalaXmlNode.Element(elem, _) => for {
      attr <- elem.attributes
    } yield ScalaXmlNode.Attribute(attr, parent)
    case _                                      => Nil
  }).asJava
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = parent match {
    case parent: ScalaXmlNode.Parent =>
      val newAttr = Attribute(Some(attribute.getPrefix).filter(_.nonEmpty), attribute.getLocalPart, Text(""), Null)
      parent transform { _ % newAttr }
      ScalaXmlNode.Attribute(newAttr, parent)
    case _                           =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case parent: ScalaXmlNode.Parent =>
      val newElem = Elem(Some(element.getPrefix).filter(_.nonEmpty).orNull, element.getLocalPart, Null, parent.node.scope, minimizeEmpty = true)
      parent transform { elem => elem.copy(child = elem.child ++ newElem) }
      ScalaXmlNode.Element(newElem, parent)
    case _                           =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case parent: ScalaXmlNode.Parent                                                  =>
      parent transform { elem => elem.copy(child = elem.child.filter(_.isInstanceOf[Text]) ++ Text(text)) }
    case ScalaXmlNode.Attribute(attribute: Attribute, parent) if attribute.isPrefixed =>
      val newAttr = Attribute(Some(attribute.pre), attribute.key, Text(text), Null)
      parent transform { _ % newAttr }
    case ScalaXmlNode.Attribute(attribute, parent)                                    =>
      val newAttr = Attribute(None, attribute.key, Text(text), Null)
      parent transform { _ % newAttr }
  }
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case ScalaXmlNode.Element(toCopy, parent) =>
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
    case _                                                          =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  override def remove(node: ScalaXmlNode): Unit = node match {
    case ScalaXmlNode.Element(toDelete, parent)                                     =>
      parent transform { elem => elem.copy(child = elem.child filter (_ eq toDelete)) }
    case ScalaXmlNode.Attribute(toDelete: Attribute, parent) if toDelete.isPrefixed =>
      parent transform { elem => elem.copy(attributes = elem.attributes remove
        (toDelete.getNamespace(elem), elem, toDelete.key)) }
    case ScalaXmlNode.Attribute(toDelete, parent)                                   =>
      parent transform { elem => elem.copy(attributes = elem.attributes remove toDelete.key) }
    case _                                                                          =>
      throw new XmlBuilderException(s"Unable to delete node $node")
  }
}
