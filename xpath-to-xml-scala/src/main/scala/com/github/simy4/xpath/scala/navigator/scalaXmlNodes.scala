package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import _root_.scala.xml.{ Elem, MetaData, Text, Attribute => XmlAttribute }

/**
  * Scala XML node contract.
  *
  * @author Alex Simkin
  * @since 2.0
  */
sealed trait ScalaXmlNode extends NavigatorNode {
  val parent: Parent
  def elements: Iterable[ScalaXmlNode]
  def attributes: Iterable[ScalaXmlNode]
}
@SerialVersionUID(1L)
sealed abstract class Parent extends ScalaXmlNode with Serializable {
  private[navigator] var node: Elem
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
}

@SerialVersionUID(1L)
final class Root(override var node: Elem) extends Parent {
  override def getName: QName = new QName(NavigatorNode.DOCUMENT)
  override val parent: Parent = null
  override def elements: Iterable[Element] = new Element(node, 0, this) :: Nil
  override def attributes: Iterable[Attribute] = Nil
  override def equals(obj: Any): Boolean = obj match {
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
  override def attributes: Iterable[Attribute] = _node.attributes.view.map(new Attribute(_, this))
  override private[navigator] def node: Elem = _node
  override private[navigator] def node_=(elem: Elem): Unit = {
    val parentNode = parent.node
    parent.node =
      if (parentNode eq _node) elem
      else parentNode.copy(child = parentNode.child.updated(index, elem))
    _node = elem
  }
  override def equals(obj: Any): Boolean = obj match {
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
final class Attribute private[navigator](private[this] var _meta: MetaData, override val parent: Parent)
    extends ScalaXmlNode
    with Serializable {
  override def getName: QName = _meta match {
    case a : XmlAttribute if a.isPrefixed => new QName(a.getNamespace(parent.node), a.key, a.pre)
    case _                                => new QName(_meta.key)
  }
  override def getText: String = _meta.value.text
  override def elements: Iterable[Element] = Nil
  override def attributes: Iterable[Attribute] = Nil
  override def equals(obj: Any): Boolean = obj match {
    case a: Attribute => _meta == a.meta
    case _            => false
  }
  override def hashCode(): Int = _meta.hashCode()
  override def toString: String = _meta.toString
  private[navigator] def meta: MetaData = _meta
  private[navigator] def meta_=(meta: MetaData): Unit = {
    parent.node = parent.node % meta
    _meta = meta
  }
}

object Attribute {
  def apply(meta: MetaData, parent: Parent): Attribute = {
    parent.node = parent.node % meta
    new Attribute(meta, parent)
  }
}
