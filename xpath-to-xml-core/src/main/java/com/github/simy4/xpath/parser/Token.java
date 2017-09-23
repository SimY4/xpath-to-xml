package com.github.simy4.xpath.parser;

import com.google.errorprone.annotations.Immutable;

@Immutable
final class Token {

    private final Type type;
    private final String xpath;
    private final int beginIndex;
    private final int endIndex;

    Token(Type type, String xpath, int beginIndex, int endIndex) {
        this.type = type;
        this.xpath = xpath;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    Type getType() {
        return type;
    }

    String getToken() {
        return xpath.substring(beginIndex, endIndex);
    }

    @Override
    public String toString() {
        return "{" + type + "}" + xpath.substring(beginIndex, endIndex);
    }

    enum Type {
        EOF,
        SKIP,
        ERROR,

        EQUALS,
        NOT_EQUALS,

        LESS_THAN,
        LESS_THAN_OR_EQUALS,
        GREATER_THAN,
        GREATER_THAN_OR_EQUALS,

        PLUS,
        MINUS,
        STAR,

        SLASH,
        DOUBLE_SLASH,
        DOT,
        DOUBLE_DOT,

        IDENTIFIER,

        AT,
        COLON,

        LEFT_BRACKET,
        RIGHT_BRACKET,

        LITERAL,

        DOUBLE
    }

}
