package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.parser.Token.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class XPathLexerTest {

    private static Stream<Arguments> data() {
        // Examples from https://msdn.microsoft.com/en-us/library/ms256086(v=vs.110).aspx
        return Stream.of(
                Arguments.of("./author", asList(token(Type.DOT, "."), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "author"))),
                Arguments.of("author", singletonList(token(Type.IDENTIFIER, "author"))),
                Arguments.of("first.name", singletonList(token(Type.IDENTIFIER, "first.name"))),
                Arguments.of("/bookstore", asList(token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"))),
                Arguments.of("book[/bookstore/@specialty=@style]", asList(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"),
                        token(Type.SLASH, "/"), token(Type.AT, "@"), token(Type.IDENTIFIER, "specialty"),
                        token(Type.EQUALS, "="), token(Type.AT, "@"), token(Type.IDENTIFIER, "style"),
                        token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author/first-name", asList(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "first-name"))),
                Arguments.of("bookstore/*/title", asList(token(Type.IDENTIFIER, "bookstore"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "title"))),
                Arguments.of("author/*", asList(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"))),
                Arguments.of("book/*/last-name", asList(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "last-name"))),
                Arguments.of("*/*", asList(token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.STAR, "*"))),
                Arguments.of("*[@specialty]", asList(token(Type.STAR, "*"), token(Type.LEFT_BRACKET, "["),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "specialty"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("@style", asList(token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))),
                Arguments.of("price/@exchange", asList(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "exchange"))),
                Arguments.of("price/@exchange/total", asList(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "exchange"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "total"))),
                Arguments.of("book[@style]", asList(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "style"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("book/@style", asList(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))),
                Arguments.of("@*", asList(token(Type.AT, "@"), token(Type.STAR, "*"))),
                Arguments.of("./first-name", asList(token(Type.DOT, "."), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "first-name"))),
                Arguments.of("first-name", singletonList(token(Type.IDENTIFIER, "first-name"))),
                Arguments.of("author[1]", asList(token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[first-name][3]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "first-name"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["), token(Type.DOUBLE, "3"),
                        token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("my:book", asList(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.IDENTIFIER, "book"))),
                Arguments.of("my:*", asList(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.STAR, "*"))),
                Arguments.of("@my:*", asList(token(Type.AT, "@"), token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.STAR, "*"))),
                Arguments.of("book[excerpt]", asList(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "excerpt"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("book[excerpt]/title", asList(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "excerpt"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "title"))),
                Arguments.of("book[excerpt]/author[degree]", asList(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "excerpt"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "degree"),
                        token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("book[author/degree]", asList(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "degree"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[degree][award]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "degree"), token(Type.RIGHT_BRACKET, "]"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "award"),
                        token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[last-name = \"Bob\"]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "last-name"), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("degree[@from != \"Harvard\"]", asList(token(Type.IDENTIFIER, "degree"),
                        token(Type.LEFT_BRACKET, "["), token(Type.AT, "@"), token(Type.IDENTIFIER, "from"),
                        token(Type.NOT_EQUALS, "!="), token(Type.LITERAL, "Harvard"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[. = \"Matthew Bob\"]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.DOT, "."), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Matthew Bob"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[last-name[1] = \"Bob\"]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "last-name"),
                        token(Type.LEFT_BRACKET, "["), token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"),
                        token(Type.EQUALS, "="), token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("author[* = \"Bob\"]", asList(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.STAR, "*"), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("ancestor::book[1]", asList(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("ancestor::book[author][1]", asList(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "author"), token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                Arguments.of("ancestor::author[parent::book][1]", asList(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "parent"), token(Type.DOUBLE_COLON, "::"),
                        token(Type.IDENTIFIER, "book"), token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]")))
        );
    }

    @ParameterizedTest(name = "Given XPath {0} should tokenize it into {1}")
    @MethodSource("data")
    void shouldTokenizeXPath(String xpath, Collection<Token> expectedTokens) {
        Iterator<Token> expectedTokensIterator = expectedTokens.iterator();
        Iterator<Token> actualTokensIterator = new XPathLexer(xpath);
        while (actualTokensIterator.hasNext()) {
            assertThat(expectedTokensIterator.hasNext()).as("expected to have more tokens").isTrue();
            Token expectedToken = expectedTokensIterator.next();
            Token actualToken = actualTokensIterator.next();
            assertThat(actualToken.getType()).isEqualTo(expectedToken.getType());
            assertThat(actualToken.getToken()).isEqualTo(expectedToken.getToken());
        }
        assertThat(expectedTokensIterator.hasNext()).as("expected to have no more tokens").isFalse();
    }

    private static Token token(short type, String token) {
        return new Token(type, token, 0, token.length());
    }

}