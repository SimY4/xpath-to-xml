package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import _root_.scala.xml.{ Elem, MetaData, Text, Attribute => XmlAttribute }

sealed trait ScalaXmlNode extends NavigatorNode

object ScalaXmlNode {
  private[navigator] sealed trait Parent extends ScalaXmlNode {
    def node: Elem
    @inline def transform(transformation: Elem => Elem): Unit
  }
  private[navigator] object Parent {
    def unapply(arg: Parent): Option[Elem] = Some(arg.node)
  }

  case class Root(private var _node: Elem) extends Parent {
    override def getName: QName = throw new UnsupportedOperationException("getName")
    override def getText: String = throw new UnsupportedOperationException("getText")
    override def node: Elem = _node
    override def transform(transformation: Elem => Elem): Unit = {
      _node = transformation(_node)
    }
  }

  private[navigator] case class Element(private var _node: Elem, parent: Parent) extends Parent {
    override def getName: QName = {
      val curNode = node
      Option(curNode.prefix).fold(new QName(curNode.label))(new QName(curNode.namespace, curNode.label, _))
    }
    override def getText: String = node.child.collect { case Text(t) => t }.mkString
    override def node: Elem = _node
    override def transform(transformation: Elem => Elem): Unit = {
      val oldNode = _node
      val newNode = transformation(oldNode)
      if (oldNode xml_!= newNode) {
        parent match {
          case _: Root    => parent transform (_ => newNode)
          case _: Element => parent transform { elem =>
            val children = elem.child.toList
            val idx = children indexOf oldNode
            val newChildren = children patch (idx, Seq(newNode), 1)
            elem.copy(child = newChildren)
          }
        }
      }
      _node = newNode
    }
  }

  private[navigator] case class Attribute(meta: MetaData, parent: Parent) extends ScalaXmlNode {
    override val getName: QName = {
      meta match {
        case a : XmlAttribute if a.isPrefixed => new QName(a.getNamespace(parent.node), a.key, a.pre)
        case _                                => new QName(meta.key)
      }
    }
    override def getText: String = meta.value.text
  }
}
