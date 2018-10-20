package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import _root_.scala.xml.{ Elem, MetaData, Attribute => XmlAttribute }

sealed trait ScalaXmlNode extends NavigatorNode

object ScalaXmlNode {
  sealed trait Parent extends ScalaXmlNode {
    def node: Elem
    @inline def transform(transformation: Elem => Elem): Unit
  }
  private[navigator] object Parent {
    def unapply(arg: Parent): Option[Elem] = Some(arg.node)
  }

  case class Root(private var _node: Elem) extends Parent {
    override def getName: QName = throw new UnsupportedOperationException("getName")
    override def getText: String = node.text
    override def node: Elem = _node
    override def transform(transformation: Elem => Elem): Unit = {
      _node = transformation(_node)
    }
  }

  private[navigator] case class Element(private var _node: Elem, parent: Parent) extends Parent {
    override def getName: QName = {
      val curNode = node
      Option(curNode.prefix) match {
        case Some(pre) => new QName(curNode.namespace, curNode.label, pre)
        case None      => new QName(curNode.label)
      }
    }
    override def getText: String = node.text
    override def node: Elem = _node
    override def transform(transformation: Elem => Elem): Unit = {
      val children = parent.node.child.toList
      val idx = children indexOf _node
      _node = transformation(_node)
      val newChildren = children patch (idx, Seq(_node), 1)
      val newParent = parent.node.copy(child = newChildren)
      parent transform (_ => newParent)
    }
  }

  private[navigator] case class Attribute(var meta: MetaData, parent: Parent) extends ScalaXmlNode {
    override def getName: QName = {
      meta match {
        case a : XmlAttribute if a.isPrefixed => new QName(a.getNamespace(parent.node), a.key, a.pre)
        case _                                => new QName(meta.key)
      }
    }
    override def getText: String = meta.value.text
  }
}
