package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.{ Node => NavigatorNode }

import _root_.scala.xml.{ Elem, MetaData, Attribute => XmlAttribute }

sealed trait ScalaXmlNode extends NavigatorNode

object ScalaXmlNode {
  sealed trait ScalaXmlParent extends ScalaXmlNode {
    val elem: Elem
  }

  case class Document(override val elem: Elem) extends ScalaXmlParent {
    override def getName: QName = throw new UnsupportedOperationException("getName")
    override def getText: String = elem.text
  }

  case class Element(override val elem: Elem, parent: ScalaXmlParent) extends ScalaXmlParent {
    override val getName: QName = Option(elem.prefix) match {
      case Some(pre) => new QName(elem.namespace, elem.label, pre)
      case None      => new QName(elem.label)
    }
    override def getText: String = elem.text
  }

  case class Attribute(attribute: MetaData, parent: ScalaXmlParent) extends ScalaXmlNode {
    override val getName: QName = attribute match {
      case a : XmlAttribute if a.isPrefixed => new QName(attribute.getNamespace(parent.elem), a.key, a.pre)
      case attr                             => new QName(attr.key)
    }
    override def getText: String = attribute.value.text
  }
}
