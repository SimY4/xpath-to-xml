/*
 * Copyright 2021 Alex Simkin
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
