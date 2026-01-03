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

import navigator.Node
import xml.{ Attribute => XmlAttribute, Elem, Text }

import javax.xml.namespace.QName

/**
 * Scala XML node contract.
 *
 * @author
 *   Alex Simkin
 * @since 2.0
 */
sealed trait ScalaXmlNode extends Node with Product with Serializable {
  type N
  def parent: Option[ScalaXmlNode]
  @SuppressWarnings(Array("org.wartremover.warts.Var"))
  private[navigator] var node: N
  def elements: Iterable[ScalaXmlNode]
  def attributes: Iterable[ScalaXmlNode]
  override def toString: String = node.toString
}

@SerialVersionUID(1L)
@SuppressWarnings(Array("org.wartremover.warts.Var"))
final case class Root(var node: Elem) extends ScalaXmlNode {
  type N = Elem
  def getName: QName                = new QName(Node.DOCUMENT)
  def getText: String               = ""
  def parent: None.type             = None
  def elements: Iterable[Element]   = Element(node)(Some(this)) :: Nil
  def attributes: Iterable[Nothing] = Nil
}

@SerialVersionUID(1L)
@SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.DefaultArguments"))
final private[navigator] case class Element(
  private var _node: Elem,
  var index: Int = 0
)(var parent: Option[ScalaXmlNode { type N = Elem }] = None)
    extends ScalaXmlNode {
  type N = Elem
  def getName: QName = {
    val node = _node
    if (null != node.prefix) new QName(node.namespace, node.label, node.prefix)
    else new QName(node.label)
  }
  def getText: String = node.child.collect { case Text(t) => t }.mkString
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def elements: Iterable[Element] =
    _node.child.view.zipWithIndex.collect { case (e: Elem, i) => Element(e, i)(Some(this)) }
  def attributes: Iterable[Attribute] =
    _node.attributes.view.collect { case a: XmlAttribute => Attribute(a)(Some(this)) }
  private[navigator] def node: Elem = _node
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  @throws[XmlBuilderException]("If setting an element to detached node")
  private[navigator] def node_=(elem: Elem): Unit = {
    val parent     = this.parent.getOrElse(throw new XmlBuilderException("Unable to set element to detached node"))
    val parentNode = parent.node
    parent.node =
      if (parentNode eq _node) elem
      else parentNode.copy(child = parentNode.child.updated(index, elem))
    _node = elem
  }
}

@SerialVersionUID(1L)
@SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.DefaultArguments"))
final private[navigator] case class Attribute(
  private var attr: XmlAttribute
)(var parent: Option[ScalaXmlNode { type N = Elem }] = None)
    extends ScalaXmlNode {
  type N = XmlAttribute
  def getName: QName = {
    val attr = this.attr
    parent match {
      case Some(parent) if attr.isPrefixed => new QName(attr.getNamespace(parent.node), attr.key, attr.pre)
      case _                               => new QName(attr.key)
    }
  }
  def getText: String                       = attr.value.text
  def elements: Iterable[Nothing]           = Nil
  def attributes: Iterable[Nothing]         = Nil
  private[navigator] def node: XmlAttribute = attr
  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  @throws[XmlBuilderException]("If setting an attribute to detached node")
  private[navigator] def node_=(attr: XmlAttribute): Unit = {
    val parent = this.parent.getOrElse(throw new XmlBuilderException("Unable to set attribute to detached node"))
    parent.node = parent.node % attr
    this.attr = attr
  }
}
