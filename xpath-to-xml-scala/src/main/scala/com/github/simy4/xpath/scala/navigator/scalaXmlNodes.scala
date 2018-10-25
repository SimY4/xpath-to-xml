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
  override def elements: Iterable[Element] = Seq(Element(node, 0, this))
  override def attributes: Iterable[Attribute] = Nil
}

private[navigator] case class Element(private var _node: Elem, var index: Int,
                                      override val parent: Parent) extends Parent {
  override def getName: QName = {
    val curNode = node
    Option(curNode.prefix).fold(new QName(curNode.label))(new QName(curNode.namespace, curNode.label, _))
  }
  override def getText: String = node.child.collect { case Text(t) => t }.mkString
  override def elements: Iterable[Element] = for {
    (n, i) <- _node.child.zipWithIndex if !n.isAtom
  } yield Element(n.asInstanceOf[Elem], i, this)
  override def attributes: Iterable[Attribute] = _node.attributes map (Attribute(_, this))
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
}

private[navigator] case class Attribute(var meta: MetaData, override val parent: Parent) extends ScalaXmlNode {
  override val getName: QName = {
    meta match {
      case a : XmlAttribute if a.isPrefixed => new QName(a.getNamespace(parent.node), a.key, a.pre)
      case _                                => new QName(meta.key)
    }
  }
  override def getText: String = meta.value.text
  override def elements: Iterable[Element] = Nil
  override def attributes: Iterable[Attribute] = Nil
}
