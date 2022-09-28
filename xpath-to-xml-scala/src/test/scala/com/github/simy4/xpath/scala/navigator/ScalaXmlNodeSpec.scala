/*
 * Copyright 2021-2022 Alex Simkin
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
import org.scalatest.matchers.should.Matchers

import javax.xml.namespace.QName

@SuppressWarnings(Array("org.wartremover.warts.IterableOps", "org.wartremover.warts.Null"))
class ScalaXmlNodeSpec extends AnyFunSpec with Matchers {
  val xml = <root attr="value">text</root>

  describe("XML root") {
    val root = new Root(xml)

    it("should get root node")(root.node should ===(xml))
    it("should set another root") {
      val root        = new Root(xml)
      val anotherRoot = <another_root/>
      root.node = anotherRoot
      root.node shouldBe theSameInstanceAs(anotherRoot)
    }

    describe("equality") {
      val parent    = root.elements.head
      val attribute = parent.attributes.head

      it("root can equal to parent")(root.canEqual(parent) shouldBe true)
      it("root can equal to attribute")(root.canEqual(attribute) shouldBe true)
      it("root is not equal to parent")(root.equals(attribute) shouldBe false)
      it("root is not equal to attribute")(root.equals(attribute) shouldBe false)
      it("parent can equal to root")(parent.canEqual(root) shouldBe false)
      it("parent can equal to attribute")(parent.canEqual(attribute) shouldBe false)
      it("parent is not equal to root")(parent.equals(root) shouldBe false)
      it("parent is not equal to attribute")(parent.equals(attribute) shouldBe false)
      it("attribute can equal to root")(attribute.canEqual(root) shouldBe true)
      it("attribute can equal to parent")(attribute.canEqual(parent) shouldBe true)
      it("attribute is not equal to root")(attribute.equals(root) shouldBe false)
      it("attribute is not equal to parent")(attribute.equals(parent) shouldBe false)
    }

    it("should return document name")((root.getName: @noinline) should ===(new QName(Node.DOCUMENT)))
    it("should return empty text")((root.getText: @noinline) should ===(""))
    it("should return root element when elements accessed") {
      root.elements should contain only new Element(xml, 0, root)
    }
    it("should return Nil when attributes accessed")((root.attributes: @noinline) shouldBe Nil)
    it("should return null parent")(root.parent shouldBe null)
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(root)

      it("should can equal to root")(deserializedNode.canEqual(root) shouldBe true)
      it("should equal to root")(deserializedNode.equals(root) shouldBe true)
      it("should has same hashcode as root")(deserializedNode.hashCode() should ===(root.hashCode()))
      it("should has same toString as root")(deserializedNode.toString() should ===(root.toString()))
    }
  }

  describe("XML element") {
    val root    = new Root(xml)
    val element = root.elements.head

    it("should return parent")(element.parent should ===(root))
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(element)

      it("should can equal to element")(deserializedNode.canEqual(element) shouldBe true)
      it("should equal to element")(deserializedNode.equals(element) shouldBe true)
      it("should has same hashcode as element")(deserializedNode.hashCode() should ===(element.hashCode()))
      it("should has same toString as element")(deserializedNode.toString() should ===(element.toString()))
    }
  }

  describe("XML attribute") {
    val root      = new Root(xml)
    val parent    = root.elements.head
    val attribute = parent.attributes.head

    it("should return parent")(attribute.parent should ===(parent))
    it("should return Nil when elements accessed")((attribute.elements: @noinline) shouldBe Nil)
    it("should return Nil when attributes accessed")((attribute.attributes: @noinline) shouldBe Nil)
    describe("when serialize and deserialize") {
      val deserializedNode = SerializationHelper.serializeAndDeserializeBack(attribute)

      it("should can equal to attribute")(deserializedNode.canEqual(attribute) shouldBe true)
      it("should equal to attribute")(deserializedNode.equals(attribute) shouldBe true)
      it("should has same hashcode as attribute")(deserializedNode.hashCode() should ===(attribute.hashCode()))
      it("should has same toString as attribute")(deserializedNode.toString() should ===(attribute.toString()))
    }
  }
}
