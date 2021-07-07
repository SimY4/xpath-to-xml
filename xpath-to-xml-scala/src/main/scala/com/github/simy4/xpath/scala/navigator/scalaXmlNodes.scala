/*
 * Copyright 2018-2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
abstract private[navigator] class AbstractScalaXmlNode protected (val parent: ScalaXmlNode)
    extends ScalaXmlNode
    with Serializable {
  def canEqual(that: Any): Boolean = that.isInstanceOf[ScalaXmlNode]
  override def equals(that: Any): Boolean =
    that match {
      case n: ScalaXmlNode => n.canEqual(this) && node == n.node
      case _               => false
    }
  override def hashCode(): Int  = node.hashCode()
  override def toString: String = node.toString
}

@SerialVersionUID(1L)
final class Root(var node: Elem) extends AbstractScalaXmlNode(null) {
  type N = Elem
  def getName: QName                  = new QName(NavigatorNode.DOCUMENT)
  def getText: String                 = ""
  def elements: Iterable[Element]     = new Element(node, 0, this) :: Nil
  def attributes: Iterable[Attribute] = Nil
}

@SerialVersionUID(1L)
final class Element private[navigator] (
  private[this] var _node: Elem,
  var index: Int,
  override val parent: ScalaXmlNode { type N = Elem }
) extends AbstractScalaXmlNode(parent) {
  type N = Elem
  def getName: QName = {
    val node = _node
    if (null != node.prefix) new QName(node.namespace, node.label, node.prefix)
    else new QName(node.label)
  }
  def getText: String = node.child.collect { case Text(t) => t }.mkString
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def elements: Iterable[Element] =
    _node.child.view.zipWithIndex.collect { case (e: Elem, i) => new Element(e, i, this) }
  def attributes: Iterable[Attribute] =
    _node.attributes.view.collect { case a: XmlAttribute => new Attribute(a, this) }
  private[navigator] def node: Elem = _node
  private[navigator] def node_=(elem: Elem): Unit = {
    val parentNode = parent.node
    parent.node =
      if (parentNode eq _node) elem
      else parentNode.copy(child = parentNode.child.updated(index, elem))
    _node = elem
  }
  override def canEqual(that: Any): Boolean = that.isInstanceOf[Element]
  override def equals(that: Any): Boolean =
    that match {
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
final class Attribute private[navigator] (
  private[this] var _attr: XmlAttribute,
  override val parent: ScalaXmlNode {
    type N = Elem
  }
) extends AbstractScalaXmlNode(parent) {
  type N = XmlAttribute
  def getName: QName = {
    val attr = _attr
    if (attr.isPrefixed) new QName(attr.getNamespace(parent.node), attr.key, attr.pre)
    else new QName(attr.key)
  }
  def getText: String                       = _attr.value.text
  def elements: Iterable[Element]           = Nil
  def attributes: Iterable[Attribute]       = Nil
  private[navigator] def node: XmlAttribute = _attr
  private[navigator] def node_=(attr: XmlAttribute): Unit = {
    parent.node = parent.node % attr
    _attr = attr
  }
}
