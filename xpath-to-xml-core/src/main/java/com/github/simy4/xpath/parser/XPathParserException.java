/*
 * Copyright 2017-2021 Alex Simkin
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
package com.github.simy4.xpath.parser;

import javax.xml.xpath.XPathExpressionException;

import java.util.Arrays;

class XPathParserException extends XPathExpressionException {

  private static final long serialVersionUID = 1L;

  XPathParserException(String xpath, Token actual, short expected, short... restExpected) {
    super(
        "Unable to parse xpath:\n"
            + xpath
            + padding(actual)
            + "Expected tokens: "
            + Arrays.toString(Token.Type.lookup(expected, restExpected))
            + ". Actual: "
            + actual.getToken());
  }

  private static String padding(Token token) {
    StringBuilder padding = new StringBuilder(128);
    padding.append('\n');
    for (int i = 0; i < token.getBeginIndex(); i++) {
      padding.append(' ');
    }
    padding.append("╰─▪ ");
    return padding.toString();
  }
}
