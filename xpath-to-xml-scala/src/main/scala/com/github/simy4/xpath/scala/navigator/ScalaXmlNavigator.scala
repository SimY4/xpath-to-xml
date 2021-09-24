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
import navigator.Navigator

import xml.{ Attribute => XmlAttribute, Elem, Null, Text }

class ScalaXmlNavigator(override val root: Root) extends Navigator[ScalaXmlNode] {
  import scala.compat.Converters._

  override def parentOf(node: ScalaXmlNode): ScalaXmlNode                                = node.parent
  override def elementsOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode]   = parent.elements
  override def attributesOf(parent: ScalaXmlNode): java.lang.Iterable[_ <: ScalaXmlNode] = parent.attributes
  @throws[XmlBuilderException]("If unable to create attribute for given node")
  override def createAttribute(parent: ScalaXmlNode, attribute: QName): ScalaXmlNode =
    parent match {
      case e: Element =>
        val pre  = attribute.getPrefix
        val attr = XmlAttribute(if (pre.nonEmpty) Some(pre) else None, attribute.getLocalPart, Text(""), Null)
        e.node = e.node % attr
        new Attribute(attr, e)
      case _ =>
        throw new XmlBuilderException(s"Unable to create attribute for ${parent.toString}")
    }
  @throws[XmlBuilderException]("If unable to create element for given node")
  override def createElement(parent: ScalaXmlNode, element: QName): ScalaXmlNode =
    parent match {
      case e: Element =>
        val node     = e.node
        val children = node.child
        val pre      = element.getPrefix
        val elem = Elem(if (pre.nonEmpty) pre else null, element.getLocalPart, Null, node.scope, minimizeEmpty = true)
        e.node = node.copy(child = children :+ elem)
        new Element(elem, children.size, e)
      case _ =>
        throw new XmlBuilderException(s"Unable to create element for ${parent.toString}")
    }
  @throws[XmlBuilderException]("If unable to set text to given node")
  override def setText(node: ScalaXmlNode, text: String): Unit =
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
  @throws[XmlBuilderException]("If unable to prepend copy to given node")
  override def prependCopy(node: ScalaXmlNode): Unit =
    node match {
      case e: Element =>
        val toCopy     = e.node
        val copy       = toCopy.copy()
        val idx        = e.index
        val parentNode = e.parent.node
        e.parent.node = parentNode.copy(child = parentNode.child.patch(idx, Seq(copy, toCopy), 1))
        e.index += 1
      case _ =>
        throw new XmlBuilderException(s"Unable to prepend copy to ${node.toString}")
    }
  @throws[XmlBuilderException]("If unable to remove given node")
  override def remove(node: ScalaXmlNode): Unit =
    node match {
      case e: Element =>
        val idx        = e.index
        val parentNode = e.parent.node
        e.parent.node = parentNode.copy(child = parentNode.child.patch(idx, Nil, 1))
      case a: Attribute =>
        val toDelete   = a.node
        val parentNode = a.parent.node
        val newAttr =
          if (toDelete.isPrefixed)
            parentNode.attributes.remove(toDelete.getNamespace(parentNode), parentNode, toDelete.key)
          else parentNode.attributes.remove(toDelete.key)
        a.parent.node = parentNode.copy(attributes = newAttr)
      case _ =>
        throw new XmlBuilderException(s"Unable to remove node ${node.toString}")
    }
}
