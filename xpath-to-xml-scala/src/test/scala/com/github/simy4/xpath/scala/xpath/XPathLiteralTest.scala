package com.github.simy4.xpath
package scala.xpath

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import parser.XPathParser

class XPathLiteralTest {
  import scala.implicits._

  @Test
  def xpathLiteralShouldCompileStringIntoExpr(): Unit = {
    val expr = xpath"ancestor::author[parent::book][1]"

    assertThat(expr).hasToString(new XPathParser(null).parse("ancestor::author[parent::book][1]").toString)
  }

}
