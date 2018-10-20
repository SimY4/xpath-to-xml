package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Navigator

import _root_.scala.xml.transform.RewriteRule
import _root_.scala.xml.{ Attribute, Elem, Node, Null, Text }

class ScalaXmlNavigator(xml: ScalaXmlNode.Document) extends RewriteRule with Navigator[ScalaXmlNode] {

  private val transformations = new java.util.IdentityHashMap[Node, Node]()

  override val root: ScalaXmlNode.Document = xml.copy(update(xml.elem))
  override def parentOf(node: ScalaXmlNode): ScalaXmlNode = node match {
    case ScalaXmlNode.Document(_)                                       => null
    case ScalaXmlNode.Element(_, parent @ ScalaXmlNode.Document(_))     => parent.copy(elem = update(parent.elem))
    case ScalaXmlNode.Element(_, parent @ ScalaXmlNode.Element(_, _))   => parent.copy(elem = update(parent.elem))
    case ScalaXmlNode.Attribute(_, parent @ ScalaXmlNode.Document(_))   => parent.copy(elem = update(parent.elem))
    case ScalaXmlNode.Attribute(_, parent @ ScalaXmlNode.Element(_, _)) => parent.copy(elem = update(parent.elem))
  }
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case parent @ ScalaXmlNode.Document(elem)          => Seq(ScalaXmlNode.Element(elem, parent))
    case elem @ ScalaXmlNode.Element(node, _) => for {
      child     <- node \ "_"
      childElem <- child match {
        case el: Elem => Seq(el)
        case _        => Nil
      }
    } yield ScalaXmlNode.Element(childElem, elem)
    case ScalaXmlNode.Attribute(_, _)         => Nil
  }).asJava
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = (parent match {
    case elem @ ScalaXmlNode.Element(node, _) => for {
      attr <- node.attributes
    } yield ScalaXmlNode.Attribute(attr, elem)
    case _                                    => Nil
  }).asJava
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = parent match {
    case parent @ ScalaXmlNode.Element(elem, _) =>
      val newAttr = Attribute(Some(attribute.getPrefix).filter(_.nonEmpty), attribute.getLocalPart, Text(""), Null)
      val newParent = elem % newAttr
      transformations.put(elem, newParent)
      ScalaXmlNode.Attribute(newAttr, parent)
    case _                                        =>
      throw new XmlBuilderException(s"Unable to create attribute for $parent")
  }
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = parent match {
    case parent @ ScalaXmlNode.Element(elem, _) =>
      val newElem = Elem(
        Some(element.getPrefix).filter(_.nonEmpty).orNull,
        element.getLocalPart,
        Null,
        elem.scope,
        minimizeEmpty = true
      )
      val newParent = elem.copy(child = elem.child ++ newElem)
      transformations.put(elem, newParent)
      ScalaXmlNode.Element(newElem, parent)
    case _                                        =>
      throw new XmlBuilderException(s"Unable to create element for $parent")
  }
  override def setText(node: ScalaXmlNode, text: String): Unit = node match {
    case parent @ ScalaXmlNode.Element(elem, _)                                       =>
      val newParent = elem.copy(child = elem.child.filter(_.isInstanceOf[Text]) ++ Text(text))
      transformations.put(elem, newParent)
    case ScalaXmlNode.Attribute(attribute: Attribute, parent) if attribute.isPrefixed =>
      val newAttr = Attribute(Some(attribute.pre), attribute.key, Text(text), Null)
      val updatedParent = update(parent)
      val newParent = updatedParent.elem % newAttr
      transformations.put(updatedParent.elem, newParent)
    case ScalaXmlNode.Attribute(attribute, parent)                                    =>
      val newAttr = Attribute(None, attribute.key, Text(text), Null)
      val updatedParent = update(parent)
      val newParent = updatedParent.elem % newAttr
      transformations.put(updatedParent.elem, updatedParent)
  }
  override def prependCopy(node: ScalaXmlNode): Unit = node match {
    case ScalaXmlNode.Element(elem, ScalaXmlNode.Element(parent, _)) =>
      val newElem = elem.copy()
      val updatedParent = update(parent)
      val newParent = updatedParent.copy(child = updatedParent.child ++ newElem)
      transformations.put(updatedParent, newParent)
    case _                                                           =>
      throw new XmlBuilderException(s"Unable to prepend copy to $node")
  }
  override def remove(node: ScalaXmlNode): Unit = node match {
    case ScalaXmlNode.Element(elem, ScalaXmlNode.Element(parent, _))                                 =>
      val updatedParent = update(parent)
      val newParent = updatedParent.copy(child = updatedParent.child filter (_ eq elem))
      transformations.put(updatedParent, newParent)
    case ScalaXmlNode.Attribute(attr: Attribute, ScalaXmlNode.Element(parent, _)) if attr.isPrefixed =>
      val updatedParent = update(parent)
      val newParent = updatedParent.copy(attributes = updatedParent.attributes remove
        (attr.getNamespace(parent), parent, attr.key))
      transformations.put(updatedParent, newParent)
    case ScalaXmlNode.Attribute(attr, ScalaXmlNode.Element(parent, _))                               =>
      val updatedParent = update(parent)
      val newParent = updatedParent.copy(attributes = updatedParent.attributes remove attr.key)
      transformations.put(updatedParent, newParent)
    case _                                                                                           =>
      throw new XmlBuilderException(s"Unable to delete node $node")
  }
  private def update[T <: Node] (t: T): T =
    Option(transformations.get(t)).map(_.asInstanceOf[T]) getOrElse t
  override def transform(n: Node): Seq[Node] = if (transformations.isEmpty) n else {
    super.transform(Option(transformations.get(n)) getOrElse n)
  }
}
