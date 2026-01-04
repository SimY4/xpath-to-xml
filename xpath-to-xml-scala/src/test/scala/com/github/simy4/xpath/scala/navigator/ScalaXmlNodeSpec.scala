/*
 * Copyright 2021-2025 Alex Simkin
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

import helpers.SerializationHelper
import navigator.Node
import org.scalatest.funspec.AnyFunSpec

import javax.xml.namespace.QName

@SuppressWarnings(Array("org.wartremover.warts.IterableOps", "org.wartremover.warts.Null"))
class ScalaXmlNodeSpec extends AnyFunSpec {
  val xml = <root attr="value">text</root>

  describe("XML root") {
    val root = new Root(xml)

    it("should get root node")(assert(root.node === xml))
    it("should set another root") {
      val root        = new Root(xml)
      val anotherRoot = <another_root/>
      root.node = anotherRoot
      assert(root.node eq anotherRoot)
    }

    describe("equality") {
      val parent    = root.elements.head
      val attribute = parent.attributes.head

      it("root can equal to parent")(assert(root.canEqual(parent)))
      it("root can equal to attribute")(assert(root.canEqual(attribute)))
      it("root is not equal to parent")(assert(!root.equals(attribute)))
      it("root is not equal to attribute")(assert(!root.equals(attribute)))
      it("parent can equal to root")(assert(!parent.canEqual(root)))
      it("parent can equal to attribute")(assert(!parent.canEqual(attribute)))
      it("parent is not equal to root")(assert(!parent.equals(root)))
      it("parent is not equal to attribute")(assert(!parent.equals(attribute)))
      it("attribute can equal to root")(assert(attribute.canEqual(root)))
      it("attribute can equal to parent")(assert(attribute.canEqual(parent)))
      it("attribute is not equal to root")(assert(!attribute.equals(root)))
      it("attribute is not equal to parent")(assert(!attribute.equals(parent)))
    }

    it("should return document name")(assert((root.getName: @noinline) === new QName(Node.DOCUMENT)))
    it("should return empty text")(assert((root.getText: @noinline) === ""))
    it("should return root element when elements accessed") {
      assert(root.elements === List(new Element(xml, 0, root)))
    }
    it("should return Nil when attributes accessed")(assert((root.attributes: @noinline) === Nil))
    it("should return null parent")(assert(root.parent === null))
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(root)

      it("should can equal to root")(assert(deserializedNode.canEqual(root)))
      it("should equal to root")(assert(deserializedNode.equals(root)))
      it("should has same hashcode as root")(assert(deserializedNode.hashCode() === root.hashCode()))
      it("should has same toString as root")(assert(deserializedNode.toString() === root.toString()))
    }
  }

  describe("XML element") {
    val root    = new Root(xml)
    val element = root.elements.head

    it("should return parent")(assert(element.parent === root))
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(element)

      it("should can equal to element")(assert(deserializedNode.canEqual(element)))
      it("should equal to element")(assert(deserializedNode.equals(element)))
      it("should has same hashcode as element")(assert(deserializedNode.hashCode() === element.hashCode()))
      it("should has same toString as element")(assert(deserializedNode.toString() === element.toString()))
    }
  }

  describe("XML attribute") {
    val root      = new Root(xml)
    val parent    = root.elements.head
    val attribute = parent.attributes.head

    it("should return parent")(assert(attribute.parent === parent))
    it("should return Nil when elements accessed")(assert((attribute.elements: @noinline) === Nil))
    it("should return Nil when attributes accessed")(assert((attribute.attributes: @noinline) === Nil))
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(attribute)

      it("should can equal to attribute")(assert(deserializedNode.canEqual(attribute)))
      it("should equal to attribute")(assert(deserializedNode.equals(attribute)))
      it("should has same hashcode as attribute")(assert(deserializedNode.hashCode() === attribute.hashCode()))
      it("should has same toString as attribute")(assert(deserializedNode.toString() === attribute.toString()))
    }
  }
}
