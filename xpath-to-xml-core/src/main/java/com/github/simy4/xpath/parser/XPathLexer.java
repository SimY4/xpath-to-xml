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

import java.util.Iterator;

@SuppressWarnings("BadImport")
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
      final var ch = charAt(1);
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
        case '+':
          token = new Token(Type.PLUS, xpath, cursor, cursor + 1);
          cursor += 1;
          break;
        case '-':
          token = new Token(Type.MINUS, xpath, cursor, cursor + 1);
          cursor += 1;
          break;
        case '<':
        case '>':
          token = relationalOperator();
          break;
        case '=':
          token = new Token(Type.EQUALS, xpath, cursor, cursor + 1);
          cursor += 1;
          break;
        case '!':
          if ('=' == charAt(2)) {
            token = new Token(Type.NOT_EQUALS, xpath, cursor, cursor + 2);
            cursor += 2;
          } else {
            token = null;
          }
          break;
        case '@':
          token = new Token(Type.AT, xpath, cursor, cursor + 1);
          cursor += 1;
          break;
        case ':':
          token = token2(':', Type.COLON, Type.DOUBLE_COLON);
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
        token =
            hasNext()
                ? new Token(Type.ERROR, xpath, cursor, xpath.length())
                : new Token(Type.EOF, xpath, 0, 0);
      }
    } while (token.getType() == Type.SKIP);
    return token;
  }

  private Token literal() {
    final var match = charAt(1);
    final var start = cursor += 1;
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
    final var start = cursor;
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
    final var start = cursor;
    var periodAllowed = true;
    loop:
    while (true) {
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

  private Token relationalOperator() {
    final Token token;
    switch (charAt(1)) {
      case '<':
        if ('=' == charAt(2)) {
          token = new Token(Type.LESS_THAN_OR_EQUALS, xpath, cursor, cursor + 2);
          cursor += 1;
        } else {
          token = new Token(Type.LESS_THAN, xpath, cursor, cursor + 1);
        }
        cursor += 1;
        break;
      case '>':
        if ('=' == charAt(2)) {
          token = new Token(Type.GREATER_THAN_OR_EQUALS, xpath, cursor, cursor + 2);
          cursor += 1;
        } else {
          token = new Token(Type.GREATER_THAN, xpath, cursor, cursor + 1);
        }
        cursor += 1;
        break;
      default:
        token = null;
        break;
    }
    return token;
  }

  private Token whitespace() {
    loop:
    while (true) {
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

  private Token token2(char ch, short if1, short if2) {
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

  @SuppressWarnings("NegativeCharLiteral")
  private char charAt(int i) {
    final var pos = cursor + i - 1;
    return pos >= xpath.length() ? (char) -1 : xpath.charAt(pos);
  }

  private boolean isXmlStartCharacter(char ch) {
    return Character.isLetter(ch) || '_' == ch;
  }

  private boolean isXmlCharacter(char ch) {
    return isXmlStartCharacter(ch) || Character.isDigit(ch) || '-' == ch || '.' == ch;
  }
}
