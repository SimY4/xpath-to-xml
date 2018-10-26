package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import _root_.scala.xml.{ Elem, MetaData, Text, Attribute => XmlAttribute }

sealed trait ScalaXmlNode extends NavigatorNode {
  val parent: Parent
  def elements: Iterable[Element]
  def attributes: Iterable[Attribute]
}
private[navigator] sealed trait Parent extends ScalaXmlNode {
  var node: Elem
}

final class Root(override var node: Elem) extends Parent {
  override def getName: QName = throw new UnsupportedOperationException("getName")
  override def getText: String = throw new UnsupportedOperationException("getText")
  override val parent: Parent = null
  override def elements: Iterable[Element] = Seq(new Element(node, 0, this))
  override def attributes: Iterable[Attribute] = Nil
  override def equals(obj: Any): Boolean = obj match {
    case r: Root => node == r.node
    case _       => false
  }
  override def hashCode(): Int = node.hashCode()
  override def toString: String = node.toString
}

private[navigator] final class Element(private var _node: Elem, var index: Int,
                                      override val parent: Parent) extends Parent {
  override def getName: QName = node match {
    case prefixed if null != prefixed.prefix => new QName(prefixed.namespace, prefixed.label, prefixed.prefix)
    case simple                              => new QName(simple.label)
  }
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
  override def elements: Iterable[Element] = for {
    (n, i) <- _node.child.zipWithIndex if !n.isAtom
  } yield new Element(n.asInstanceOf[Elem], i, this)
  override def attributes: Iterable[Attribute] = _node.attributes map (new Attribute(_, this))
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
    case e: Element => _node == e._node && index == e.index
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

private[navigator] final class Attribute(var meta: MetaData, override val parent: Parent) extends ScalaXmlNode {
  override def getName: QName = meta match {
    case a : XmlAttribute if a.isPrefixed => new QName(a.getNamespace(parent.node), a.key, a.pre)
    case _                                => new QName(meta.key)
  }
  override def getText: String = meta.value.text
  override def elements: Iterable[Element] = Nil
  override def attributes: Iterable[Attribute] = Nil
  override def equals(obj: Any): Boolean = obj match {
    case a: Attribute => meta == a.meta
    case _            => false
  }
  override def hashCode(): Int = meta.hashCode()
  override def toString: String = meta.toString
}
