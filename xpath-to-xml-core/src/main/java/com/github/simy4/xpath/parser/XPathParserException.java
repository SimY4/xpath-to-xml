package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.parser.Token.Type;

import javax.xml.xpath.XPathExpressionException;
import java.util.Arrays;

class XPathParserException extends XPathExpressionException {

    XPathParserException(Token actual) {
        super("Expected no more tokens but was: " + actual);
    }

    XPathParserException(Token actual, Type... expected) {
        super("Expected tokens: " + Arrays.toString(expected) + " but was: " + actual);
    }

}
