package com.github.simy4.xpath.parser;

import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;

final class Token {

    private final short type;
    private final String xpath;
    private final int beginIndex;
    private final int endIndex;

    Token(short type, String xpath, int beginIndex, int endIndex) {
        this.type = type;
        this.xpath = xpath;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    short getType() {
        return type;
    }

    String getToken() {
        return xpath.substring(beginIndex, endIndex);
    }

    @Override
    public String toString() {
        return xpath.substring(beginIndex, endIndex);
    }

    static final class Type {
        static final short EOF = -1;
        static final short SKIP = -2;
        static final short ERROR = -3;

        static final short EQUALS = 1;
        static final short NOT_EQUALS = 2;

        static final short LESS_THAN = 3;
        static final short LESS_THAN_OR_EQUALS = 4;
        static final short GREATER_THAN = 5;
        static final short GREATER_THAN_OR_EQUALS = 6;

        static final short PLUS = 7;
        static final short MINUS = 8;
        static final short STAR = 9;

        static final short SLASH = 12;
        static final short DOUBLE_SLASH = 13;
        static final short DOT = 14;
        static final short DOUBLE_DOT = 15;

        static final short IDENTIFIER = 16;

        static final short AT = 17;
        static final short COLON = 19;
        static final short DOUBLE_COLON = 20;

        static final short LEFT_BRACKET = 21;
        static final short RIGHT_BRACKET = 22;

        static final short LITERAL = 26;

        static final short DOUBLE = 29;

        private static final Map<Short, String> LOOKUP_MAP = Map.ofEntries(
                entry(EOF, "<eof>"),
                entry(SKIP, "<skip>"),
                entry(ERROR, "<error>"),
                entry(EQUALS, "'='"),
                entry(NOT_EQUALS, "'!='"),
                entry(LESS_THAN, "'<'"),
                entry(LESS_THAN_OR_EQUALS, "'<='"),
                entry(GREATER_THAN, "'>'"),
                entry(GREATER_THAN_OR_EQUALS, "'>='"),
                entry(PLUS, "'+'"),
                entry(MINUS, "'-'"),
                entry(STAR, "'*'"),
                entry(SLASH, "'/'"),
                entry(DOUBLE_SLASH, "'//'"),
                entry(DOT, "'.'"),
                entry(DOUBLE_DOT, "'..'"),
                entry(IDENTIFIER, "<identifier>"),
                entry(AT, "'@'"),
                entry(COLON, "':'"),
                entry(DOUBLE_COLON, "'::'"),
                entry(LEFT_BRACKET, "'['"),
                entry(RIGHT_BRACKET, "']'"),
                entry(LITERAL, "<literal>"),
                entry(DOUBLE, "<number>"));

        static String lookup(short type) {
            return requireNonNull(LOOKUP_MAP.get(type), "Unknown token type: " + type);
        }

        static String[] lookup(short... types) {
            var result = new String[types.length];
            for (var i = 0; i < types.length; i++) {
                result[i] = lookup(types[i]);
            }
            return result;
        }

        private Type() {
            throw new UnsupportedOperationException("new");
        }
    }

}
