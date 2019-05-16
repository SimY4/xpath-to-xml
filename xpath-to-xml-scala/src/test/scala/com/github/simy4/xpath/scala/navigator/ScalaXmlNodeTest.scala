package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Node
import org.assertj.core.api.{AbstractAssert, Assertions}
import org.junit.jupiter.api.Test

class ScalaXmlNodeTest {
  import Assertions._
  import ScalaXmlNodeTest._

  private val xml = <root attr="value">text</root>
  private val root = new Root(xml)

  @Test
  def shouldReturnDocumentName(): Unit =
    assertThat(root.getName).isEqualTo(new QName(Node.DOCUMENT))

  @Test
  def shouldReturnRootElementTextRootTextAccessed(): Unit =
    assertThat(root.getText).isEqualTo("text")

  @Test
  def shouldReturnRootElementWhenRootElementsAccessed(): Unit =
    moreAssertThat(root.elements).containsExactly(new Element(xml, 0, root))

  @Test
  def shouldReturnNilWhenRootAttributesAccessed(): Unit =
    moreAssertThat(root.attributes).isEmpty

  @Test
  def shouldReturnNullWhenRootParentAccessed(): Unit =
    assertThat(root.parent).isNull()

  @Test
  def shouldReturnParentWhenElementParentAccessed(): Unit = {
    val element = root.elements.head

    assertThat(element.parent).isEqualTo(root)
  }

  @Test
  def shouldReturnParentWhenAttributeParentAccessed(): Unit = {
    val parent = root.elements.head
    val attribute = parent.attributes.head

    assertThat(attribute.parent).isEqualTo(parent)
  }

  @Test
  def shouldReturnNilWhenAttributeElementsAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    moreAssertThat(attribute.elements).isEmpty
  }

  @Test
  def shouldReturnNilWhenAttributeAttributesAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    moreAssertThat(attribute.attributes).isEmpty
  }

}

object ScalaXmlNodeTest {
  private[scala] def moreAssertThat[A](it: Iterable[A]): IterableAssert[A] = new IterableAssert[A](it)

  private[scala] final class IterableAssert[A](it: Iterable[A])
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
