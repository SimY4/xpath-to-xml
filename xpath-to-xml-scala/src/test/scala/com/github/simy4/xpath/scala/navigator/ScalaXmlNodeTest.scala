package com.github.simy4.xpath
package scala.navigator

import helpers.SerializationHelper
import javax.xml.namespace.QName
import navigator.Node
import org.assertj.core.api.{ AbstractAssert, Assertions }
import org.junit.jupiter.api.Test

class ScalaXmlNodeTest {
  import Assertions._
  import ScalaXmlNodeTest._

  private val xml  = <root attr="value">text</root>
  private val root = new Root(xml)

  @Test
  def shouldGetAndSetRoot(): Unit = {
    assertThat(root.node).isEqualTo(xml)
    val anotherRoot = <another_root/>
    root.node = anotherRoot
    assertThat(root.node).isNotEqualTo(xml).isEqualTo(anotherRoot)
  }

  @Test
  def checkEquality(): Unit = {
    val parent    = root.elements.head
    val attribute = parent.attributes.head

    assertThat(root.canEqual(parent)).isTrue
    assertThat(root.canEqual(attribute)).isTrue
    assertThat(root).isNotEqualTo(parent).isNotEqualTo(attribute)
    assertThat(parent.canEqual(root)).isFalse
    assertThat(parent.canEqual(attribute)).isFalse
    assertThat(parent).isNotEqualTo(root).isNotEqualTo(attribute)
    assertThat(attribute.canEqual(parent)).isTrue
    assertThat(attribute.canEqual(attribute)).isTrue
    assertThat(attribute).isNotEqualTo(root).isNotEqualTo(parent)
  }

  @Test
  def shouldReturnDocumentName(): Unit =
    assertThat(root.getName: @noinline).isEqualTo(new QName(Node.DOCUMENT))

  @Test
  def shouldReturnEmptyTextWhenRootTextAccessed(): Unit =
    assertThat(root.getText: @noinline).isEqualTo("")

  @Test
  def shouldReturnRootElementWhenRootElementsAccessed(): Unit =
    moreAssertThat(root.elements).containsExactly(new Element(xml, 0, root))

  @Test
  def shouldReturnNilWhenRootAttributesAccessed(): Unit =
    moreAssertThat(root.attributes: @noinline).isEmpty

  @Test
  def shouldReturnNullWhenRootParentAccessed(): Unit =
    assertThat(root.parent).isNull()

  @Test
  def shouldReturnParentWhenElementParentAccessed(): Unit = {
    val element = root.elements.head

    assertThat(element.parent).isEqualTo(root)
  }

  @Test
  def shouldSerializeAndDeserializeRoot(): Unit = {
    val deserializedNode = SerializationHelper.serializeAndDeserializeBack(root)

    assertThat(deserializedNode.canEqual(root)).isTrue()
    assertThat(deserializedNode).isEqualTo(root).hasSameHashCodeAs(root).hasToString(root.toString())
  }

  @Test
  def shouldReturnParentWhenAttributeParentAccessed(): Unit = {
    val parent    = root.elements.head
    val attribute = parent.attributes.head

    assertThat(attribute.parent).isEqualTo(parent)
  }

  @Test
  def shouldSerializeAndDeserializeElement(): Unit = {
    val deserializedNode = SerializationHelper.serializeAndDeserializeBack(root.elements.head)

    assertThat(deserializedNode.canEqual(root.elements.head)).isTrue()
    assertThat(deserializedNode).isEqualTo(root.elements.head)
    assertThat(deserializedNode.hashCode()).isEqualTo(root.elements.head.hashCode())
  }

  @Test
  def shouldReturnNilWhenAttributeElementsAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    moreAssertThat(attribute.elements: @noinline).isEmpty
  }

  @Test
  def shouldReturnNilWhenAttributeAttributesAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    moreAssertThat(attribute.attributes: @noinline).isEmpty
  }

  @Test
  def shouldSerializeAndDeserializeAttribute(): Unit = {
    val deserializedNode = SerializationHelper.serializeAndDeserializeBack(root.elements.head.attributes.head)

    assertThat(deserializedNode.canEqual(root.elements.head.attributes.head)).isTrue()
    assertThat(deserializedNode).isEqualTo(root.elements.head.attributes.head)
    assertThat(deserializedNode.hashCode()).isEqualTo(root.elements.head.attributes.head.hashCode())
  }

}

object ScalaXmlNodeTest {
  private[scala] def moreAssertThat[A](it: Iterable[A]): IterableAssert[A] = new IterableAssert[A](it)

  final private[scala] class IterableAssert[A](it: Iterable[A])
      extends AbstractAssert[IterableAssert[A], Iterable[A]](it, classOf[IterableAssert[A]]) {
    def isEmpty: IterableAssert[A] = {
      isNotNull
      if (actual.nonEmpty) failWithMessage("%nExpecting empty but was:<%s>", actual)
      this
    }
    def containsExactly[AA >: A](as: AA*): IterableAssert[A] = {
      isNotNull
      if (actual.toList != as.toList) failWithMessage("%nExpecting <%s> but was:<%s>", as, actual)
      this
    }
  }
}
