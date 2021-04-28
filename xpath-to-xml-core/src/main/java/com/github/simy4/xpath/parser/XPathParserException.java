package com.github.simy4.xpath.parser;

import javax.xml.xpath.XPathExpressionException;

import java.util.Arrays;

class XPathParserException extends XPathExpressionException {

  private static final long serialVersionUID = 1L;

  XPathParserException(Token actual) {
    super("Expected no more tokens but was: " + actual);
  }

  XPathParserException(Token actual, String... expected) {
    super("Expected tokens: " + Arrays.toString(expected) + " but was: " + actual);
  }
}
