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
  def elements: Iterable[Element]
  def attributes: Iterable[Attribute]
}
private[navigator] sealed trait Parent extends ScalaXmlNode {
  var node: Elem
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
}

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

private[navigator] final class Element(private[this] var _node: Elem, var index: Int,
                                       override val parent: Parent) extends Parent {
  override def getName: QName = _node match {
    case prefixed if null != prefixed.prefix => new QName(prefixed.namespace, prefixed.label, prefixed.prefix)
    case simple                              => new QName(simple.label)
  }
  override def elements: Iterable[Element] = for {
    (elem, i) <- _node.child.view.zipWithIndex collect { case (e: Elem, i) => e -> i }
  } yield new Element(elem, i, this)
  override def attributes: Iterable[Attribute] = _node.attributes.view map (new Attribute(_, this))
  override def node: Elem = _node
  override def node_=(elem: Elem): Unit = {
    parent.node = parent match {
      case _: Root    => elem
      case _: Element =>
        val newChildren = parent.node.child patch (index, Seq(elem), 1)
        parent.node.copy(child = newChildren)
    }
    _node = elem
  }
  override def equals(obj: Any): Boolean = obj match {
    case e: Element => _node == e.node && index == e.index
    case _          => false
  }
  override def hashCode(): Int = {
    val prime = 31
    var result = 1
    result = prime * result + _node.hashCode()
    result = prime * result + index
    result
  }
  override def toString: String = _node.toString
}

private[navigator] final class Attribute(private[this] var _meta: MetaData, override val parent: Parent) extends ScalaXmlNode {
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
  def meta: MetaData = _meta
  def meta_=(meta: MetaData): Unit = {
    parent.node = parent.node % meta
    _meta = meta
  }
}
