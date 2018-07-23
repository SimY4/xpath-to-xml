package com.github.simy4.xpath.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

        private static final Map<Short, String> LOOKUP_MAP;

        static {
            Map<Short, String> lookupMap = new HashMap<Short, String>();
            lookupMap.put(EOF, "<eof>");
            lookupMap.put(SKIP, "<skip>");
            lookupMap.put(ERROR, "<error>");
            lookupMap.put(EQUALS, "'='");
            lookupMap.put(NOT_EQUALS, "'!='");
            lookupMap.put(LESS_THAN, "'<'");
            lookupMap.put(LESS_THAN_OR_EQUALS, "'<='");
            lookupMap.put(GREATER_THAN, "'>'");
            lookupMap.put(GREATER_THAN_OR_EQUALS, "'>='");
            lookupMap.put(PLUS, "'+'");
            lookupMap.put(MINUS, "'-'");
            lookupMap.put(STAR, "'*'");
            lookupMap.put(SLASH, "'/'");
            lookupMap.put(DOUBLE_SLASH, "'//'");
            lookupMap.put(DOT, "'.'");
            lookupMap.put(DOUBLE_DOT, "'..'");
            lookupMap.put(IDENTIFIER, "<identifier>");
            lookupMap.put(AT, "'@'");
            lookupMap.put(COLON, "':'");
            lookupMap.put(DOUBLE_COLON, "'::'");
            lookupMap.put(LEFT_BRACKET, "'['");
            lookupMap.put(RIGHT_BRACKET, "']'");
            lookupMap.put(LITERAL, "<literal>");
            lookupMap.put(DOUBLE, "<number>");
            LOOKUP_MAP = Collections.unmodifiableMap(lookupMap);
        }

        static String lookup(short type) {
            String result = LOOKUP_MAP.get(type);
            if (null == result) {
                throw new IllegalArgumentException("Unknown token type: " + type);
            }
            return result;
        }

        static String[] lookup(short... types) {
            String[] result = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                result[i] = lookup(types[i]);
            }
            return result;
        }

        private Type() { }
    }

}
