package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{Node => NavigatorNode}

import xml.{Elem, Text, Attribute => XmlAttribute}

/**
 * Scala XML node contract.
 *
 * @author Alex Simkin
 * @since 2.0
 */
sealed trait ScalaXmlNode extends NavigatorNode {
  def parent: Parent
  def elements: Iterable[ScalaXmlNode]
  def attributes: Iterable[ScalaXmlNode]
}
@SerialVersionUID(1L)
sealed abstract class Parent extends ScalaXmlNode with Serializable with Equals {
  private[navigator] var node: Elem
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
}

@SerialVersionUID(1L)
final class Root(override var node: Elem) extends Parent {
  override def getName: QName = new QName(NavigatorNode.DOCUMENT)
  override def parent: Parent = null
  override def elements: Iterable[Element] = new Element(node, 0, this) :: Nil
  override def attributes: Iterable[Attribute] = Nil
  override def canEqual(that: Any): Boolean = that.isInstanceOf[Root]
  override def equals(that: Any): Boolean = that match {
    case r: Root => node == r.node
    case _       => false
  }
  override def hashCode(): Int = node.hashCode()
  override def toString: String = node.toString
}

@SerialVersionUID(1L)
final class Element private[navigator](private[this] var _node: Elem, var index: Int, override val parent: Parent)
    extends Parent {
  override def getName: QName = _node match {
    case prefixed if null != prefixed.prefix => new QName(prefixed.namespace, prefixed.label, prefixed.prefix)
    case simple                              => new QName(simple.label)
  }
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
    case e: Element => _node == e.node && index == e.index
    case _          => false
  }
  override def hashCode(): Int = {
    var result = 1
    result = 31 * result + _node.hashCode()
    result = 31 * result + index
    result
  }
  override def toString: String = _node.toString
}

object Element {
  def apply(elem: Elem, parent: Parent): Element = {
    val node = parent.node
    val children = node.child
    parent.node = node.copy(child = children :+ elem)
    new Element(elem, children.size, parent)
  }
}

@SerialVersionUID(1L)
final class Attribute private[navigator](private[this] var _attr: XmlAttribute, override val parent: Parent)
    extends ScalaXmlNode
    with Serializable
    with Equals {
  override def getName: QName =
    if (_attr.isPrefixed) new QName(_attr.getNamespace(parent.node), _attr.key, _attr.pre)
    else new QName(_attr.key)
  override def getText: String = _attr.value.text
  override def elements: Iterable[Element] = Nil
  override def attributes: Iterable[Attribute] = Nil
  override def canEqual(that: Any): Boolean = that.isInstanceOf[Attribute]
  override def equals(that: Any): Boolean = that match {
    case a: Attribute => _attr == a.attr
    case _            => false
  }
  override def hashCode(): Int = _attr.hashCode()
  override def toString: String = _attr.toString
  private[navigator] def attr: XmlAttribute = _attr
  private[navigator] def attr_=(attr: XmlAttribute): Unit = {
    parent.node = parent.node % attr
    _attr = attr
  }
}

object Attribute {
  def apply(attr: XmlAttribute, parent: Parent): Attribute = {
    parent.node = parent.node % attr
    new Attribute(attr, parent)
  }
}
