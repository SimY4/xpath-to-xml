package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import xml.{ Elem, Text, Attribute => XmlAttribute }

/**
 * Scala XML node contract.
 *
 * @author Alex Simkin
 * @since 2.0
 */
sealed trait ScalaXmlNode extends NavigatorNode with Equals {
  type N
  val parent: ScalaXmlNode
  private[navigator] var node: N
  def elements: Iterable[ScalaXmlNode]
  def attributes: Iterable[ScalaXmlNode]
}

@SerialVersionUID(1L)
abstract private[navigator] class AbstractScalaXmlNode protected (override val parent: ScalaXmlNode)
    extends ScalaXmlNode
    with Serializable {
  override def canEqual(that: Any): Boolean = that.isInstanceOf[ScalaXmlNode]
  override def equals(that: Any): Boolean = that match {
    case n: ScalaXmlNode => n.canEqual(this) && node == n.node
    case _               => false
  }
  override def hashCode(): Int  = node.hashCode()
  override def toString: String = node.toString
}

@SerialVersionUID(1L)
final class Root(override var node: Elem) extends AbstractScalaXmlNode(null) {
  override type N = Elem
  override def getName: QName                  = new QName(NavigatorNode.DOCUMENT)
  override def getText: String                 = ""
  override def elements: Iterable[Element]     = new Element(node, 0, this) :: Nil
  override def attributes: Iterable[Attribute] = Nil
}

@SerialVersionUID(1L)
final class Element private[navigator] (
  private[this] var _node: Elem,
  var index: Int,
  override val parent: ScalaXmlNode { type N = Elem }
) extends AbstractScalaXmlNode(parent) {
  override type N = Elem
  override def getName: QName = {
    val node = _node
    if (null != node.prefix) new QName(node.namespace, node.label, node.prefix)
    else new QName(node.label)
  }
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  override def elements: Iterable[Element] =
    _node.child.view.zipWithIndex.collect { case (e: Elem, i) => new Element(e, i, this) }
  override def attributes: Iterable[Attribute] =
    _node.attributes.view.collect { case a: XmlAttribute => new Attribute(a, this) }
  override private[navigator] def node: Elem = _node
  override private[navigator] def node_=(elem: Elem): Unit = {
    val parentNode = parent.node
    parent.node =
      if (parentNode eq _node) elem
      else parentNode.copy(child = parentNode.child.updated(index, elem))
    _node = elem
  }
  override def canEqual(that: Any): Boolean = that.isInstanceOf[Element]
  override def equals(that: Any): Boolean = that match {
    case e: Element => e.canEqual(this) && index == e.index && super.equals(that)
    case _          => false
  }
  override def hashCode(): Int = {
    var result = super.hashCode()
    result = 31 * result + index
    result
  }
}

@SerialVersionUID(1L)
final class Attribute private[navigator] (private[this] var _attr: XmlAttribute, override val parent: ScalaXmlNode {
  type N = Elem
}) extends AbstractScalaXmlNode(parent) {
  override type N = XmlAttribute
  override def getName: QName = {
    val attr = _attr
    if (attr.isPrefixed) new QName(attr.getNamespace(parent.node), attr.key, attr.pre)
    else new QName(attr.key)
  }
  override def getText: String                       = _attr.value.text
  override def elements: Iterable[Element]           = Nil
  override def attributes: Iterable[Attribute]       = Nil
  override private[navigator] def node: XmlAttribute = _attr
  override private[navigator] def node_=(attr: XmlAttribute): Unit = {
    parent.node = parent.node % attr
    _attr = attr
  }
}
