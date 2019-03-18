package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.parser.Token.Type;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XPathLexerTest {

    static Stream<Arguments> data() {
        // Examples from https://msdn.microsoft.com/en-us/library/ms256086(v=vs.110).aspx
        return Stream.of(
                arguments("./author", List.of(token(Type.DOT, "."), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "author"))),
                arguments("author", List.of(token(Type.IDENTIFIER, "author"))),
                arguments("first.name", List.of(token(Type.IDENTIFIER, "first.name"))),
                arguments("/bookstore", List.of(token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"))),
                arguments("book[/bookstore/@specialty=@style]", List.of(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"),
                        token(Type.SLASH, "/"), token(Type.AT, "@"), token(Type.IDENTIFIER, "specialty"),
                        token(Type.EQUALS, "="), token(Type.AT, "@"), token(Type.IDENTIFIER, "style"),
                        token(Type.RIGHT_BRACKET, "]"))),
                arguments("author/first-name", List.of(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "first-name"))),
                arguments("bookstore/*/title", List.of(token(Type.IDENTIFIER, "bookstore"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "title"))),
                arguments("author/*", List.of(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"))),
                arguments("book/*/last-name", List.of(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"),
                        token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "last-name"))),
                arguments("*/*", List.of(token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.STAR, "*"))),
                arguments("*[@specialty]", List.of(token(Type.STAR, "*"), token(Type.LEFT_BRACKET, "["),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "specialty"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("@style", List.of(token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))),
                arguments("price/@exchange", List.of(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "exchange"))),
                arguments("price/@exchange/total", List.of(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "exchange"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "total"))),
                arguments("book[@style]", List.of(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "style"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("book/@style", List.of(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"),
                        token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))),
                arguments("@*", List.of(token(Type.AT, "@"), token(Type.STAR, "*"))),
                arguments("./first-name", List.of(token(Type.DOT, "."), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "first-name"))),
                arguments("first-name", List.of(token(Type.IDENTIFIER, "first-name"))),
                arguments("author[1]", List.of(token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[first-name][3]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "first-name"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["), token(Type.DOUBLE, "3"),
                        token(Type.RIGHT_BRACKET, "]"))),
                arguments("my:book", List.of(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.IDENTIFIER, "book"))),
                arguments("my:*", List.of(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.STAR, "*"))),
                arguments("@my:*", List.of(token(Type.AT, "@"), token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                        token(Type.STAR, "*"))),
                arguments("book[excerpt]", List.of(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "excerpt"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("book[excerpt]/title", List.of(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "excerpt"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "title"))),
                arguments("book[excerpt]/author[degree]", List.of(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "excerpt"),
                        token(Type.RIGHT_BRACKET, "]"), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "degree"),
                        token(Type.RIGHT_BRACKET, "]"))),
                arguments("book[author/degree]", List.of(token(Type.IDENTIFIER, "book"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                        token(Type.IDENTIFIER, "degree"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[degree][award]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "degree"), token(Type.RIGHT_BRACKET, "]"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "award"),
                        token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[last-name = \"Bob\"]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "last-name"), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("degree[@from != \"Harvard\"]", List.of(token(Type.IDENTIFIER, "degree"),
                        token(Type.LEFT_BRACKET, "["), token(Type.AT, "@"), token(Type.IDENTIFIER, "from"),
                        token(Type.NOT_EQUALS, "!="), token(Type.LITERAL, "Harvard"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[. = \"Matthew Bob\"]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.DOT, "."), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Matthew Bob"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[last-name[1] = \"Bob\"]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.IDENTIFIER, "last-name"),
                        token(Type.LEFT_BRACKET, "["), token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"),
                        token(Type.EQUALS, "="), token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("author[* = \"Bob\"]", List.of(token(Type.IDENTIFIER, "author"),
                        token(Type.LEFT_BRACKET, "["), token(Type.STAR, "*"), token(Type.EQUALS, "="),
                        token(Type.LITERAL, "Bob"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("ancestor::book[1]", List.of(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("ancestor::book[author][1]", List.of(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "author"), token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))),
                arguments("ancestor::author[parent::book][1]", List.of(token(Type.IDENTIFIER, "ancestor"),
                        token(Type.DOUBLE_COLON, "::"), token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                        token(Type.IDENTIFIER, "parent"), token(Type.DOUBLE_COLON, "::"),
                        token(Type.IDENTIFIER, "book"), token(Type.RIGHT_BRACKET, "]"), token(Type.LEFT_BRACKET, "["),
                        token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]")))
        );
    }

    @ParameterizedTest(name = "Given XPath {0} should tokenize it into {1}")
    @DisplayName("Should tokenize XPath")
    @MethodSource("data")
    void shouldTokenizeXPath(String xpath, Collection<Token> expectedTokens) {
        var expectedTokensIterator = expectedTokens.iterator();
        var actualTokensIterator = new XPathLexer(xpath);
        while (actualTokensIterator.hasNext()) {
            assertThat(expectedTokensIterator.hasNext()).as("expected to have more tokens").isTrue();
            var expectedToken = expectedTokensIterator.next();
            var actualToken = actualTokensIterator.next();
            var softly = new SoftAssertions();
            softly.assertThat(actualToken.getType()).isEqualTo(expectedToken.getType());
            softly.assertThat(actualToken.getToken()).isEqualTo(expectedToken.getToken());
            softly.assertAll();
        }
        assertThat(expectedTokensIterator.hasNext()).as("expected to have no more tokens").isFalse();
    }

    private static Token token(short type, String token) {
        return new Token(type, token, 0, token.length());
    }

}