package com.github.simy4.xpath
package scala.navigator

import javax.xml.namespace.QName
import navigator.Node
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ScalaXmlNodeTest {
  import Assertions._

  import _root_.scala.collection.JavaConverters._

  private val xml = <root attr="value">text</root>
  private val root = new Root(xml)

  @Test
  def shouldReturnDocumentName(): Unit = {
    assertThat(root.getName).isEqualTo(new QName(Node.DOCUMENT))
  }

  @Test
  def shouldReturnRootElementTextRootTextAccessed(): Unit = {
    assertThat(root.getText) isEqualTo "text"
  }

  @Test
  def shouldReturnRootElementWhenRootElementsAccessed(): Unit = {
    assertThat(root.elements.asJava) containsExactly new Element(xml, 0, root)
  }

  @Test
  def shouldReturnNilWhenRootAttributesAccessed(): Unit = {
    assertThat(root.attributes.asJava).isEmpty()
  }

  @Test
  def shouldReturnNullWhenRootParentAccessed(): Unit = {
    assertThat(root.parent).isNull()
  }

  @Test
  def shouldReturnParentWhenElementParentAccessed(): Unit = {
    val element = root.elements.head

    assertThat(element.parent) isEqualTo root
  }

  @Test
  def shouldReturnParentWhenAttributeParentAccessed(): Unit = {
    val parent = root.elements.head
    val attribute = parent.attributes.head

    assertThat(attribute.parent) isEqualTo parent
  }

  @Test
  def shouldReturnNilWhenAttributeElementsAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    assertThat(attribute.elements.asJava).isEmpty()
  }

  @Test
  def shouldReturnNilWhenAttributeAttributesAccessed(): Unit = {
    val attribute = root.elements.head.attributes.head

    assertThat(attribute.attributes.asJava).isEmpty()
  }

}
