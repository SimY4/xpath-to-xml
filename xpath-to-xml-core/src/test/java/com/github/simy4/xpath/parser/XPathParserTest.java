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

import com.github.simy4.xpath.expr.AdditionExpr;
import com.github.simy4.xpath.expr.AxisStepExpr;
import com.github.simy4.xpath.expr.EqualsExpr;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.LiteralExpr;
import com.github.simy4.xpath.expr.MultiplicationExpr;
import com.github.simy4.xpath.expr.NotEqualsExpr;
import com.github.simy4.xpath.expr.NumberExpr;
import com.github.simy4.xpath.expr.PathExpr;
import com.github.simy4.xpath.expr.Root;
import com.github.simy4.xpath.expr.StepExpr;
import com.github.simy4.xpath.expr.SubtractionExpr;
import com.github.simy4.xpath.expr.UnaryExpr;
import com.github.simy4.xpath.expr.axis.AncestorOrSelfAxisResolver;
import com.github.simy4.xpath.expr.axis.AttributeAxisResolver;
import com.github.simy4.xpath.expr.axis.AxisResolver;
import com.github.simy4.xpath.expr.axis.ChildAxisResolver;
import com.github.simy4.xpath.expr.axis.DescendantOrSelfAxisResolver;
import com.github.simy4.xpath.expr.axis.ParentAxisResolver;
import com.github.simy4.xpath.expr.axis.SelfAxisResolver;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static java.util.Arrays.asList;

class XPathParserTest {

  private static final QName ANY = new QName("*", "*");

  static Stream<NamespaceContext> namespaceContexts() {
    return Stream.of(null, new SimpleNamespaceContext());
  }

  static Stream<Arguments> positiveSimple() {
    return namespaceContexts()
        .flatMap(
            namespaceContext ->
                Stream.of(
                    arguments(
                        "./author",
                        pathExpr(
                            stepExpr(new SelfAxisResolver(ANY)),
                            stepExpr(new ChildAxisResolver(new QName("author")))),
                        namespaceContext),
                    arguments(
                        "author",
                        pathExpr(stepExpr(new ChildAxisResolver(new QName("author")))),
                        namespaceContext),
                    arguments(
                        "first.name",
                        pathExpr(stepExpr(new ChildAxisResolver(new QName("first.name")))),
                        namespaceContext),
                    arguments(
                        "/bookstore",
                        pathExpr(
                            new Root(), stepExpr(new ChildAxisResolver(new QName("bookstore")))),
                        namespaceContext),
                    arguments(
                        "//author",
                        pathExpr(
                            new Root(),
                            stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                            stepExpr(new ChildAxisResolver(new QName("author")))),
                        namespaceContext),
                    arguments(
                        "book[/bookstore/@specialty=@style]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                new EqualsExpr(
                                    pathExpr(
                                        new Root(),
                                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                                        stepExpr(
                                            new AttributeAxisResolver(new QName("specialty")))),
                                    pathExpr(
                                        stepExpr(new AttributeAxisResolver(new QName("style"))))))),
                        namespaceContext),
                    arguments(
                        "author/first-name",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("author"))),
                            stepExpr(new ChildAxisResolver(new QName("first-name")))),
                        namespaceContext),
                    arguments(
                        "bookstore//title",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                            stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                            stepExpr(new ChildAxisResolver(new QName("title")))),
                        namespaceContext),
                    arguments(
                        "bookstore/*/title",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                            stepExpr(new ChildAxisResolver(new QName("*"))),
                            stepExpr(new ChildAxisResolver(new QName("title")))),
                        namespaceContext),
                    arguments(
                        "bookstore//book/excerpt//emph",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                            stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                            stepExpr(new ChildAxisResolver(new QName("book"))),
                            stepExpr(new ChildAxisResolver(new QName("excerpt"))),
                            stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                            stepExpr(new ChildAxisResolver(new QName("emph")))),
                        namespaceContext),
                    arguments(
                        ".//title",
                        pathExpr(
                            stepExpr(new SelfAxisResolver(ANY)),
                            stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                            stepExpr(new ChildAxisResolver(new QName("title")))),
                        namespaceContext),
                    arguments(
                        "author/*",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("author"))),
                            stepExpr(new ChildAxisResolver(new QName("*")))),
                        namespaceContext),
                    arguments(
                        "book/*/last-name",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("book"))),
                            stepExpr(new ChildAxisResolver(new QName("*"))),
                            stepExpr(new ChildAxisResolver(new QName("last-name")))),
                        namespaceContext),
                    arguments(
                        "*/*",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("*"))),
                            stepExpr(new ChildAxisResolver(new QName("*")))),
                        namespaceContext),
                    arguments(
                        "*[@specialty]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("*")),
                                pathExpr(
                                    stepExpr(new AttributeAxisResolver(new QName("specialty")))))),
                        namespaceContext),
                    arguments(
                        "@style",
                        pathExpr(stepExpr(new AttributeAxisResolver(new QName("style")))),
                        namespaceContext),
                    arguments(
                        "price/@exchange",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("price"))),
                            stepExpr(new AttributeAxisResolver(new QName("exchange")))),
                        namespaceContext),
                    arguments(
                        "price/@exchange/total",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("price"))),
                            stepExpr(new AttributeAxisResolver(new QName("exchange"))),
                            stepExpr(new ChildAxisResolver(new QName("total")))),
                        namespaceContext),
                    arguments(
                        "book[@style]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("style")))))),
                        namespaceContext),
                    arguments(
                        "book/@style",
                        pathExpr(
                            stepExpr(new ChildAxisResolver(new QName("book"))),
                            stepExpr(new AttributeAxisResolver(new QName("style")))),
                        namespaceContext),
                    arguments(
                        "@*",
                        pathExpr(stepExpr(new AttributeAxisResolver(new QName("*")))),
                        namespaceContext),
                    arguments(
                        "./first-name",
                        pathExpr(
                            stepExpr(new SelfAxisResolver(ANY)),
                            stepExpr(new ChildAxisResolver(new QName("first-name")))),
                        namespaceContext),
                    arguments(
                        "first-name",
                        pathExpr(stepExpr(new ChildAxisResolver(new QName("first-name")))),
                        namespaceContext),
                    arguments(
                        "author[1]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")), new NumberExpr(1.0))),
                        namespaceContext),
                    arguments(
                        "author[first-name][3]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("first-name")))),
                                new NumberExpr(3.0))),
                        namespaceContext),
                    arguments(
                        "1 + 2 + 2 * 2 - -4",
                        new MultiplicationExpr(
                            new AdditionExpr(
                                new NumberExpr(1.0),
                                new AdditionExpr(new NumberExpr(2.0), new NumberExpr(2.0))),
                            new SubtractionExpr(
                                new NumberExpr(2.0), new UnaryExpr(new NumberExpr(4.0)))),
                        namespaceContext),
                    arguments(
                        "book[excerpt]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("excerpt")))))),
                        namespaceContext),
                    arguments(
                        "book[excerpt]/title",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("excerpt"))))),
                            stepExpr(new ChildAxisResolver(new QName("title")))),
                        namespaceContext),
                    arguments(
                        "book[excerpt]/author[degree]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("excerpt"))))),
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("degree")))))),
                        namespaceContext),
                    arguments(
                        "book[author/degree]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(
                                    stepExpr(new ChildAxisResolver(new QName("author"))),
                                    stepExpr(new ChildAxisResolver(new QName("degree")))))),
                        namespaceContext),
                    arguments(
                        "book[degree][award]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("book")),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("degree")))),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("award")))))),
                        namespaceContext),
                    arguments(
                        "author[last-name = \"Bob\"]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                new EqualsExpr(
                                    pathExpr(
                                        stepExpr(new ChildAxisResolver(new QName("last-name")))),
                                    new LiteralExpr("Bob")))),
                        namespaceContext),
                    arguments(
                        "degree[@from != \"Harvard\"]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("degree")),
                                new NotEqualsExpr(
                                    pathExpr(
                                        stepExpr(new AttributeAxisResolver(new QName("from")))),
                                    new LiteralExpr("Harvard")))),
                        namespaceContext),
                    arguments(
                        "author[. = \"Matthew Bob\"]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                new EqualsExpr(
                                    pathExpr(stepExpr(new SelfAxisResolver(ANY))),
                                    new LiteralExpr("Matthew Bob")))),
                        namespaceContext),
                    arguments(
                        "author[last-name[1] = \"Bob\"]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                new EqualsExpr(
                                    pathExpr(
                                        stepExpr(
                                            new ChildAxisResolver(new QName("last-name")),
                                            new NumberExpr(1.0))),
                                    new LiteralExpr("Bob")))),
                        namespaceContext),
                    arguments(
                        "author[* = \"Bob\"]",
                        pathExpr(
                            stepExpr(
                                new ChildAxisResolver(new QName("author")),
                                new EqualsExpr(
                                    pathExpr(stepExpr(new ChildAxisResolver(new QName("*")))),
                                    new LiteralExpr("Bob")))),
                        namespaceContext),
                    arguments(
                        "ancestor::book[1]",
                        pathExpr(
                            stepExpr(
                                new AncestorOrSelfAxisResolver(new QName("book"), false),
                                new NumberExpr(1.0))),
                        namespaceContext),
                    arguments(
                        "ancestor::book[author][1]",
                        pathExpr(
                            stepExpr(
                                new AncestorOrSelfAxisResolver(new QName("book"), false),
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("author")))),
                                new NumberExpr(1.0))),
                        namespaceContext),
                    arguments(
                        "ancestor::author[parent::book][1]",
                        pathExpr(
                            stepExpr(
                                new AncestorOrSelfAxisResolver(new QName("author"), false),
                                pathExpr(stepExpr(new ParentAxisResolver(new QName("book")))),
                                new NumberExpr(1.0))),
                        namespaceContext)));
  }

  static Stream<Arguments> positivePrefixedNoContext() {
    return Stream.of(
        arguments("my:book", pathExpr(stepExpr(new ChildAxisResolver(new QName("book"))))),
        arguments("my:*", pathExpr(stepExpr(new ChildAxisResolver(new QName("*"))))),
        arguments("@my:*", pathExpr(stepExpr(new AttributeAxisResolver(new QName("*"))))));
  }

  static Stream<Arguments> positivePrefixedWithContext() {
    return Stream.of(
        arguments(
            "my:book",
            pathExpr(
                stepExpr(
                    new ChildAxisResolver(new QName("http://www.example.com/my", "book", "my"))))),
        arguments(
            "my:*",
            pathExpr(
                stepExpr(
                    new ChildAxisResolver(new QName("http://www.example.com/my", "*", "my"))))),
        arguments(
            "@my:*",
            pathExpr(
                stepExpr(
                    new AttributeAxisResolver(
                        new QName("http://www.example.com/my", "*", "my"))))));
  }

  static Stream<Arguments> negative() {
    return namespaceContexts()
        .flatMap(
            namespaceContext ->
                Stream.of(
                    arguments("", namespaceContext),
                    arguments("...", namespaceContext),
                    arguments("//", namespaceContext),
                    arguments("///", namespaceContext),
                    arguments("my:book:com", namespaceContext),
                    arguments("bo@k", namespaceContext),
                    arguments("book[]", namespaceContext),
                    arguments("book[]]", namespaceContext),
                    arguments("book[[]", namespaceContext),
                    arguments("book[@style='value\"]", namespaceContext)));
  }

  @ParameterizedTest(name = "Given simple XPath {0} should parse it into {1} using context {2}")
  @DisplayName("Should parse XPath using context")
  @MethodSource("positiveSimple")
  void shouldParseSimpleXPath(String xpath, Expr expectedExpr, NamespaceContext context)
      throws XPathExpressionException {
    var actualExpr = new XPathParser(context).parse(xpath);
    assertThat(actualExpr).hasToString(expectedExpr.toString());
  }

  @ParameterizedTest(name = "Given prefixed XPath {0} and no context should parse it into {1}")
  @DisplayName("Should parse prefixed XPath")
  @MethodSource("positivePrefixedNoContext")
  void shouldParsePrefixedXPathWithNoContext(String xpath, Expr expectedExpr)
      throws XPathExpressionException {
    var actualExpr = new XPathParser(null).parse(xpath);
    assertThat(actualExpr).hasToString(expectedExpr.toString());
  }

  @ParameterizedTest(name = "Given prefixed XPath {0} and some context should parse it into {1}")
  @DisplayName("Should parse prefixed XPath")
  @MethodSource("positivePrefixedWithContext")
  void shouldParsePrefixedXPathWithContext(String xpath, Expr expectedExpr)
      throws XPathExpressionException {
    var actualExpr = new XPathParser(new SimpleNamespaceContext()).parse(xpath);
    assertThat(actualExpr).hasToString(expectedExpr.toString());
  }

  @ParameterizedTest(name = "Given invalid XPath {0} should fail to parse it using context {1}")
  @DisplayName("Should fail to parse malformed XPath using context")
  @MethodSource("negative")
  void shouldThrowExceptionOnParse(
      final String invalidXPath, final NamespaceContext namespaceContext) {
    assertThatThrownBy(() -> new XPathParser(namespaceContext).parse(invalidXPath))
        .isInstanceOf(XPathParserException.class)
        .hasMessageMatching("(Expected tokens.+|Expected no more tokens but was.+)");
  }

  private static Expr pathExpr(StepExpr... steps) {
    return new PathExpr(asList(steps));
  }

  private static StepExpr stepExpr(AxisResolver axisResolver, Expr... predicates) {
    return new AxisStepExpr(axisResolver, asList(predicates));
  }
}
