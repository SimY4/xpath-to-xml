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

import navigator.Navigator
import xml.{ Attribute => XmlAttribute, Elem, Null, Text, TopScope }

import javax.xml.namespace.QName

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class ScalaXmlNavigator(override val root: Root) extends Navigator[ScalaXmlNode] with scala.compat.Converters {
  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  def parentOf(node: ScalaXmlNode): ScalaXmlNode                                = node.parent.orNull
  def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[? <: ScalaXmlNode]   = parent.elements
  def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[? <: ScalaXmlNode] = parent.attributes
  def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode = {
    val pre  = attribute.getPrefix
    val attr = XmlAttribute(if (pre.nonEmpty) Some(pre) else None, attribute.getLocalPart, Text(""), Null)
    Attribute(attr)()
  }
  def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode = {
    val scope = parent.node match {
      case e: Elem => e.scope
      case _       => TopScope
    }
    val pre  = element.getPrefix
    val elem = Elem(if (pre.nonEmpty) pre else null, element.getLocalPart, Null, scope, minimizeEmpty = true)
    Element(elem)()
  }
  @throws[XmlBuilderException]("If unable to append prev element for given node")
  def appendPrev(node: ScalaXmlNode, prepend: ScalaXmlNode): Unit =
    (node, prepend) match {
      case (e: Element, p: Element) =>
        val parent     = e.parent.getOrElse(throw new XmlBuilderException("Unable to prepend to detached node"))
        val idx        = e.index
        val parentNode = parent.node
        parent.node = parentNode.copy(child = parentNode.child.patch(idx, Seq(p.node, e.node), 1))
        e.index += 1
        p.parent = Some(parent)
      case _ =>
        throw new XmlBuilderException(s"Unable to prepend to ${node.toString}")
    }
  @throws[XmlBuilderException]("If unable to append child element for given node")
  def appendChild(parent: ScalaXmlNode, node: ScalaXmlNode): Unit =
    (parent, node) match {
      case (parent: Root, elem: Element) =>
        val node  = parent.node
        val child = node.child
        parent.node = node.copy(child = child :+ elem.node)
        elem.index = child.size
        elem.parent = Some(parent)
      case (parent: Element, elem: Element) =>
        val node  = parent.node
        val child = node.child
        parent.node = node.copy(child = child :+ elem.node)
        elem.index = child.size
        elem.parent = Some(parent)
      case (parent: Element, attr: Attribute) =>
        parent.node = parent.node % attr.node
        attr.parent = Some(parent)
      case _ =>
        throw new XmlBuilderException(s"Unable to append child ${node.toString}")
    }
  @throws[XmlBuilderException]("If unable to append next element for given node")
  def appendNext(node: ScalaXmlNode, append: ScalaXmlNode): Unit =
    (node, append) match {
      case (e: Element, a: Element) =>
        val parent     = e.parent.getOrElse(throw new XmlBuilderException("Unable to append to detached node"))
        val idx        = e.index
        val parentNode = parent.node
        parent.node = parentNode.copy(child = parentNode.child.patch(idx, Seq(e.node, a.node), 1))
        e.index += 1
        a.parent = Some(parent)
      case _ =>
        throw new XmlBuilderException(s"Unable to append to ${node.toString}")
    }
  @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
  @throws[XmlBuilderException]("If unable to set text to given node")
  def setText(node: ScalaXmlNode, text: String): Unit =
    node match {
      case e: Element =>
        val elem = e.node
        e.node = elem.copy(child = elem.child.filterNot(_.isInstanceOf[Text]) :+ Text(text))
      case a: Attribute =>
        val attr = a.node
        a.node = XmlAttribute(Option(attr.pre), attr.key, Text(text), Null)
      case _ =>
        throw new XmlBuilderException(s"Unable to set text to ${node.toString}")
    }
  @throws[XmlBuilderException]("If unable to remove given node")
  def remove(node: ScalaXmlNode): Unit =
    node match {
      case e: Element =>
        val idx        = e.index
        val parent     = e.parent.getOrElse(throw new XmlBuilderException("Unable to remove detached node"))
        val parentNode = parent.node
        parent.node = parentNode.copy(child = parentNode.child.patch(idx, Nil, 1))
      case a: Attribute =>
        val toDelete   = a.node
        val parent     = a.parent.getOrElse(throw new XmlBuilderException("Unable to remove detached node"))
        val parentNode = parent.node
        val newAttr =
          if (toDelete.isPrefixed)
            parentNode.attributes.remove(toDelete.getNamespace(parentNode), parentNode, toDelete.key)
          else parentNode.attributes.remove(toDelete.key)
        parent.node = parentNode.copy(attributes = newAttr)
      case _ =>
        throw new XmlBuilderException(s"Unable to remove node ${node.toString}")
    }
}
