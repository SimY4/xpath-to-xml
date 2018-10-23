package com.github.simy4.xpath.scala.navigator

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class ScalaXmlNodeTest {
  import Assertions._

  private val xml = <root/>

  @Test
  def shouldThrowWhenRootNameAccessed(): Unit = {
    assertThatThrownBy(() => new Root(xml).getName).isInstanceOf(classOf[UnsupportedOperationException])
  }

  @Test
  def shouldThrowWhenRootTextAccessed(): Unit = {
    assertThatThrownBy(() => new Root(xml).getText).isInstanceOf(classOf[UnsupportedOperationException])
  }

}
