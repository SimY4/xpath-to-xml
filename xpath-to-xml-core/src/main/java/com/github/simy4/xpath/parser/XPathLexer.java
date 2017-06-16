package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.parser.Token.Type;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

@NotThreadSafe
class XPathLexer implements Iterator<Token> {

    private final String xpath;
    private int cursor;

    XPathLexer(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public boolean hasNext() {
        return cursor < xpath.length();
    }

    @Override
    public Token next() {
        Token token;
        do {
            char ch = charAt(1);
            switch (ch) {
                case '"':
                case '\'':
                    token = literal();
                    break;
                case '/':
                    token = token2('/', Type.SLASH, Type.DOUBLE_SLASH);
                    break;
                case '[':
                    token = new Token(Type.LEFT_BRACKET, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case ']':
                    token = new Token(Type.RIGHT_BRACKET, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case '=':
                    token = new Token(Type.EQUALS, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case '@':
                    token = new Token(Type.AT, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case ':':
                    token = new Token(Type.COLON, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case '-':
                    token = new Token(Type.MINUS, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case '*':
                    token = new Token(Type.STAR, xpath, cursor, cursor + 1);
                    cursor += 1;
                    break;
                case '.':
                    switch (charAt(2)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            token = number();
                            break;
                        default:
                            token = token2('.', Type.DOT, Type.DOUBLE_DOT);
                            break;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    token = number();
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    token = whitespace();
                    break;
                default:
                    if (isXmlStartCharacter(ch)) {
                        token = identifier();
                    } else {
                        token = null;
                    }
                    break;
            }
            if (token == null) {
                token = hasNext()
                        ? new Token(Type.ERROR, xpath, cursor, xpath.length())
                        : new Token(Type.EOF, xpath, 0, 0);
            }
        } while (token.getType() == Type.SKIP);
        return token;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Nullable
    private Token literal() {
        final char match = charAt(1);
        final int start = cursor += 1;
        Token token = null;
        while (null == token && hasNext()) {
            if (match == charAt(1)) {
                token = new Token(Type.LITERAL, xpath, start, cursor);
            }
            cursor += 1;
        }
        return token;
    }

    private Token identifier() {
        final int start = cursor;
        while (hasNext()) {
            if (isXmlCharacter(charAt(1))) {
                cursor += 1;
            } else {
                break;
            }
        }
        return new Token(Type.IDENTIFIER, xpath, start, cursor);
    }

    private Token number() {
        final int start = cursor;
        boolean periodAllowed = true;
        loop: while (true) {
            switch (charAt(1)) {
                case '.':
                    if (periodAllowed) {
                        periodAllowed = false;
                        cursor += 1;
                    } else {
                        break loop;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    cursor += 1;
                    break;
                default:
                    break loop;
            }
        }
        return new Token(Type.DOUBLE, xpath, start, cursor);
    }

    private Token whitespace() {
        loop: while (true) {
            switch (charAt(1)) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    cursor += 1;
                    break;
                default:
                    break loop;
            }
        }
        return new Token(Type.SKIP, xpath, 0, 0);
    }

    private Token token2(char ch, Type if1, Type if2) {
        final Token token;
        if (charAt(2) == ch) {
            token = new Token(if2, xpath, cursor, cursor + 2);
            cursor += 2;
        } else {
            token = new Token(if1, xpath, cursor, cursor + 1);
            cursor += 1;
        }
        return token;
    }

    private char charAt(@Nonnegative int i) {
        final int pos = cursor + i - 1;
        return pos >= xpath.length() ? (char) -1 : xpath.charAt(pos);
    }

    private boolean isXmlStartCharacter(char ch) {
        return Character.isLetter(ch) || '_' == ch;
    }

    private boolean isXmlCharacter(char ch) {
        return isXmlStartCharacter(ch) || Character.isDigit(ch) || '-' == ch || '.' == ch;
    }

}
