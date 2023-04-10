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

import com.github.simy4.xpath.parser.Token.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class XPathLexerTest {

  @SuppressWarnings("BadImport")
  static Stream<Arguments> data() {
    // Examples from https://msdn.microsoft.com/en-us/library/ms256086(v=vs.110).aspx
    return Stream.of(
        arguments(
            "./author",
            asList(token(Type.DOT, "."), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "author"))),
        arguments("author", singletonList(token(Type.IDENTIFIER, "author"))),
        arguments("first.name", singletonList(token(Type.IDENTIFIER, "first.name"))),
        arguments(
            "/bookstore", asList(token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"))),
        arguments(
            "book[/bookstore/@specialty=@style]",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "bookstore"),
                token(Type.SLASH, "/"),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "specialty"),
                token(Type.EQUALS, "="),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "style"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author/first-name",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "first-name"))),
        arguments(
            "bookstore/*/title",
            asList(
                token(Type.IDENTIFIER, "bookstore"),
                token(Type.SLASH, "/"),
                token(Type.STAR, "*"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "title"))),
        arguments(
            "author/*",
            asList(
                token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"), token(Type.STAR, "*"))),
        arguments(
            "book/*/last-name",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.SLASH, "/"),
                token(Type.STAR, "*"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "last-name"))),
        arguments(
            "*/*", asList(token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.STAR, "*"))),
        arguments(
            "*[@specialty]",
            asList(
                token(Type.STAR, "*"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "specialty"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments("@style", asList(token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))),
        arguments(
            "price/@exchange",
            asList(
                token(Type.IDENTIFIER, "price"),
                token(Type.SLASH, "/"),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "exchange"))),
        arguments(
            "price/@exchange/total",
            asList(
                token(Type.IDENTIFIER, "price"),
                token(Type.SLASH, "/"),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "exchange"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "total"))),
        arguments(
            "book[@style]",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "style"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "book/@style",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.SLASH, "/"),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "style"))),
        arguments("@*", asList(token(Type.AT, "@"), token(Type.STAR, "*"))),
        arguments(
            "./first-name",
            asList(
                token(Type.DOT, "."),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "first-name"))),
        arguments("first-name", singletonList(token(Type.IDENTIFIER, "first-name"))),
        arguments(
            "author[1]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "1"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[first-name][3]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "first-name"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "3"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "my:book",
            asList(
                token(Type.IDENTIFIER, "my"),
                token(Type.COLON, ":"),
                token(Type.IDENTIFIER, "book"))),
        arguments(
            "my:*",
            asList(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"), token(Type.STAR, "*"))),
        arguments(
            "@my:*",
            asList(
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "my"),
                token(Type.COLON, ":"),
                token(Type.STAR, "*"))),
        arguments(
            "book[excerpt]",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "excerpt"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "book[excerpt]/title",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "excerpt"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "title"))),
        arguments(
            "book[excerpt]/author[degree]",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "excerpt"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "degree"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "book[author/degree]",
            asList(
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "author"),
                token(Type.SLASH, "/"),
                token(Type.IDENTIFIER, "degree"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[degree][award]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "degree"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "award"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[last-name = \"Bob\"]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "last-name"),
                token(Type.EQUALS, "="),
                token(Type.LITERAL, "Bob"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "degree[@from != \"Harvard\"]",
            asList(
                token(Type.IDENTIFIER, "degree"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.AT, "@"),
                token(Type.IDENTIFIER, "from"),
                token(Type.NOT_EQUALS, "!="),
                token(Type.LITERAL, "Harvard"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[. = \"Matthew Bob\"]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOT, "."),
                token(Type.EQUALS, "="),
                token(Type.LITERAL, "Matthew Bob"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[last-name[1] = \"Bob\"]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "last-name"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "1"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.EQUALS, "="),
                token(Type.LITERAL, "Bob"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "author[* = \"Bob\"]",
            asList(
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.STAR, "*"),
                token(Type.EQUALS, "="),
                token(Type.LITERAL, "Bob"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "ancestor::book[1]",
            asList(
                token(Type.IDENTIFIER, "ancestor"),
                token(Type.DOUBLE_COLON, "::"),
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "1"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "ancestor::book[author][1]",
            asList(
                token(Type.IDENTIFIER, "ancestor"),
                token(Type.DOUBLE_COLON, "::"),
                token(Type.IDENTIFIER, "book"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "author"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "1"),
                token(Type.RIGHT_BRACKET, "]"))),
        arguments(
            "ancestor::author[parent::book][1]",
            asList(
                token(Type.IDENTIFIER, "ancestor"),
                token(Type.DOUBLE_COLON, "::"),
                token(Type.IDENTIFIER, "author"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.IDENTIFIER, "parent"),
                token(Type.DOUBLE_COLON, "::"),
                token(Type.IDENTIFIER, "book"),
                token(Type.RIGHT_BRACKET, "]"),
                token(Type.LEFT_BRACKET, "["),
                token(Type.DOUBLE, "1"),
                token(Type.RIGHT_BRACKET, "]"))));
  }

  @ParameterizedTest(name = "Given XPath {0} should tokenize it into {1}")
  @DisplayName("Should tokenize XPath")
  @MethodSource("data")
  void shouldTokenizeXPath(String xpath, Collection<Token> expectedTokens) {
    var expectedTokensIterator = expectedTokens.iterator();
    Iterator<Token> actualTokensIterator = new XPathLexer(xpath);
    while (actualTokensIterator.hasNext()) {
      assertThat(expectedTokensIterator.hasNext()).as("expected to have more tokens").isTrue();
      var expectedToken = expectedTokensIterator.next();
      var actualToken = actualTokensIterator.next();
      assertThat(actualToken.getType()).isEqualTo(expectedToken.getType());
      assertThat(actualToken.getToken()).isEqualTo(expectedToken.getToken());
    }
    assertThat(expectedTokensIterator.hasNext()).as("expected to have no more tokens").isFalse();
  }

  private static Token token(short type, String token) {
    return new Token(type, token, 0, token.length());
  }
}
