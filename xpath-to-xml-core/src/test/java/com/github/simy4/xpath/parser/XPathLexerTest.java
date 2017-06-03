package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.parser.Token.Type;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.Iterator;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class XPathLexerTest {

    @Parameters(name = "Given XPath {0} should tokenize it into {1}")
    public static Collection<Object[]> data() {
        // Examples from https://msdn.microsoft.com/en-us/library/ms256086(v=vs.110).aspx
        return asList(new Object[][] {
                {
                        "./author",
                        asList(token(Type.DOT, "."), token(Type.SLASH, "/"),
                                token(Type.IDENTIFIER, "author"))
                },
                {
                        "author",
                        singletonList(token(Type.IDENTIFIER, "author"))
                },
                {
                        "first.name",
                        singletonList(token(Type.IDENTIFIER, "first.name"))
                },
                {
                        "/bookstore",
                        asList(token(Type.SLASH, "/"), token(Type.IDENTIFIER, "bookstore"))
                },
                {
                        "//author",
                        asList(token(Type.DOUBLE_SLASH, "//"), token(Type.IDENTIFIER, "author"))
                },
                {
                        "book[/bookstore/@specialty=@style]",
                        asList(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["), token(Type.SLASH, "/"),
                                token(Type.IDENTIFIER, "bookstore"), token(Type.SLASH, "/"), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "specialty"), token(Type.EQUALS, "="), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "style"), token(Type.RIGHT_BRACKET, "]"))
                },
                {
                        "author/first-name",
                        asList(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"),
                                token(Type.IDENTIFIER, "first-name"))
                },
                {
                        "bookstore//title",
                        asList(token(Type.IDENTIFIER, "bookstore"), token(Type.DOUBLE_SLASH, "//"),
                                token(Type.IDENTIFIER, "title"))
                },
                {
                        "bookstore/*/title",
                        asList(token(Type.IDENTIFIER, "bookstore"), token(Type.SLASH, "/"), token(Type.STAR, "*"),
                                token(Type.SLASH, "/"), token(Type.IDENTIFIER, "title"))
                },
                {
                        "bookstore//book/excerpt//emph",
                        asList(token(Type.IDENTIFIER, "bookstore"), token(Type.DOUBLE_SLASH, "//"),
                                token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"),
                                token(Type.IDENTIFIER, "excerpt"), token(Type.DOUBLE_SLASH, "//"),
                                token(Type.IDENTIFIER, "emph"))
                },
                {
                        ".//title",
                        asList(token(Type.DOT, "."), token(Type.DOUBLE_SLASH, "//"), token(Type.IDENTIFIER, "title"))
                },
                {
                        "author/*",
                        asList(token(Type.IDENTIFIER, "author"), token(Type.SLASH, "/"), token(Type.STAR, "*"))
                },
                {
                        "book/*/last-name",
                        asList(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"), token(Type.STAR, "*"),
                                token(Type.SLASH, "/"), token(Type.IDENTIFIER, "last-name"))
                },
                {
                        "*/*",
                        asList(token(Type.STAR, "*"), token(Type.SLASH, "/"), token(Type.STAR, "*"))
                },
                {
                        "*[@specialty]",
                        asList(token(Type.STAR, "*"), token(Type.LEFT_BRACKET, "["), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "specialty"), token(Type.RIGHT_BRACKET, "]"))
                },
                {
                        "@style",
                        asList(token(Type.AT, "@"), token(Type.IDENTIFIER, "style"))
                },
                {
                        "price/@exchange",
                        asList(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "exchange"))
                },
                {
                        "price/@exchange/total",
                        asList(token(Type.IDENTIFIER, "price"), token(Type.SLASH, "/"), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "exchange"), token(Type.SLASH, "/"),
                                token(Type.IDENTIFIER, "total"))
                },
                {
                        "book[@style]",
                        asList(token(Type.IDENTIFIER, "book"), token(Type.LEFT_BRACKET, "["), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "style"), token(Type.RIGHT_BRACKET, "]"))
                },
                {
                        "book/@style",
                        asList(token(Type.IDENTIFIER, "book"), token(Type.SLASH, "/"), token(Type.AT, "@"),
                                token(Type.IDENTIFIER, "style"))
                },
                {
                        "@*",
                        asList(token(Type.AT, "@"), token(Type.STAR, "*"))
                },
                {
                        "./first-name",
                        asList(token(Type.DOT, "."), token(Type.SLASH, "/"), token(Type.IDENTIFIER, "first-name"))
                },
                {
                        "first-name",
                        singletonList(token(Type.IDENTIFIER, "first-name"))
                },
                {
                        "author[1]",
                        asList(token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                                token(Type.DOUBLE, "1"), token(Type.RIGHT_BRACKET, "]"))
                },
                {
                        "author[first-name][3]",
                        asList(token(Type.IDENTIFIER, "author"), token(Type.LEFT_BRACKET, "["),
                                token(Type.IDENTIFIER, "first-name"), token(Type.RIGHT_BRACKET, "]"),
                                token(Type.LEFT_BRACKET, "["), token(Type.DOUBLE, "3"),
                                token(Type.RIGHT_BRACKET, "]"))
                },
                {
                        "my:book",
                        asList(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"), token(Type.IDENTIFIER, "book"))
                },
                {
                        "my:*",
                        asList(token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"), token(Type.STAR, "*"))
                },
                {
                        "@my:*",
                        asList(token(Type.AT, "@"), token(Type.IDENTIFIER, "my"), token(Type.COLON, ":"),
                                token(Type.STAR, "*"))
                },
        });
    }

    @Parameter(0)
    public String xpath;
    @Parameter(1)
    public Iterable<Token> expectedTokens;

    @Test
    public void shouldTokenizeXPath() {
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

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowOnRemove() {
        new XPathLexer(xpath).remove();
    }

    private static Token token(Type type, String token) {
        return new Token(type, token, 0, token.length());
    }

}