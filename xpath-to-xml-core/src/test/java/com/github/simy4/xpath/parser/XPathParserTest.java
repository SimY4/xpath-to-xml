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
import com.github.simy4.xpath.expr.PredicateExpr;
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
import com.github.simy4.xpath.util.SimpleNamespaceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class XPathParserTest {

    private static final QName ANY = new QName("*", "*");

    private static Stream<NamespaceContext> namespaceContexts() {
        return Stream.of(
                null,
                new SimpleNamespaceContext()
        );
    }

    private static Stream<Arguments> positiveSimple() {
        return namespaceContexts().flatMap(namespaceContext -> Stream.of(
                Arguments.of("./author", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new ChildAxisResolver(new QName("author")))), namespaceContext),
                Arguments.of("author", pathExpr(stepExpr(new ChildAxisResolver(new QName("author")))),
                        namespaceContext),
                Arguments.of("first.name", pathExpr(stepExpr(new ChildAxisResolver(new QName("first.name")))),
                        namespaceContext),
                Arguments.of("/bookstore", pathExpr(
                        new Root(),
                        stepExpr(new ChildAxisResolver(new QName("bookstore")))), namespaceContext),
                Arguments.of("//author", pathExpr(
                        new Root(),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("author")))), namespaceContext),
                Arguments.of("book[/bookstore/@specialty=@style]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(new EqualsExpr(
                                pathExpr(
                                        new Root(),
                                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                                        stepExpr(new AttributeAxisResolver(new QName("specialty")))),
                                pathExpr(
                                        stepExpr(new AttributeAxisResolver(new QName("style")))))))),
                        namespaceContext),
                Arguments.of("author/first-name", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author"))),
                        stepExpr(new ChildAxisResolver(new QName("first-name")))), namespaceContext),
                Arguments.of("bookstore//title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("title")))), namespaceContext),
                Arguments.of("bookstore/*/title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("title")))), namespaceContext),
                Arguments.of("bookstore//book/excerpt//emph", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new ChildAxisResolver(new QName("excerpt"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("emph")))), namespaceContext),
                Arguments.of(".//title", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("title")))), namespaceContext),
                Arguments.of("author/*", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author"))),
                        stepExpr(new ChildAxisResolver(new QName("*")))), namespaceContext),
                Arguments.of("book/*/last-name", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("last-name")))), namespaceContext),
                Arguments.of("*/*", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("*")))), namespaceContext),
                Arguments.of("*[@specialty]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("*")), new PredicateExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("specialty"))))))),
                        namespaceContext),
                Arguments.of("@style", pathExpr(stepExpr(new AttributeAxisResolver(new QName("style")))),
                        namespaceContext),
                Arguments.of("price/@exchange", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("price"))),
                        stepExpr(new AttributeAxisResolver(new QName("exchange")))), namespaceContext),
                Arguments.of("price/@exchange/total", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("price"))),
                        stepExpr(new AttributeAxisResolver(new QName("exchange"))),
                        stepExpr(new ChildAxisResolver(new QName("total")))), namespaceContext),
                Arguments.of("book[@style]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("style"))))))),
                        namespaceContext),
                Arguments.of("book/@style", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new AttributeAxisResolver(new QName("style")))), namespaceContext),
                Arguments.of("@*", pathExpr(stepExpr(new AttributeAxisResolver(new QName("*")))),
                        namespaceContext),
                Arguments.of("./first-name", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new ChildAxisResolver(new QName("first-name")))), namespaceContext),
                Arguments.of("first-name", pathExpr(stepExpr(new ChildAxisResolver(new QName("first-name")))),
                        namespaceContext),
                Arguments.of("author[1]", pathExpr(stepExpr(new ChildAxisResolver(new QName("author")),
                        new PredicateExpr(new NumberExpr(1.0)))), namespaceContext),
                Arguments.of("author[first-name][3]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")),
                                new PredicateExpr(pathExpr(
                                        stepExpr(new ChildAxisResolver(new QName("first-name"))))),
                                new PredicateExpr(new NumberExpr(3.0)))), namespaceContext),
                Arguments.of("1 + 2 + 2 * 2 - -4", new MultiplicationExpr(new AdditionExpr(new NumberExpr(1.0),
                                new AdditionExpr(new NumberExpr(2.0), new NumberExpr(2.0))),
                                new SubtractionExpr(new NumberExpr(2.0), new UnaryExpr(new NumberExpr(4.0)))),
                        namespaceContext),
                Arguments.of("book[excerpt]", pathExpr(stepExpr(new ChildAxisResolver(new QName("book")),
                        new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("excerpt"))))))),
                        namespaceContext),
                Arguments.of("book[excerpt]/title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("excerpt")))))),
                        stepExpr(new ChildAxisResolver(new QName("title")))), namespaceContext),
                Arguments.of("book[excerpt]/author[degree]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("excerpt")))))),
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("degree"))))))), namespaceContext),
                Arguments.of("book[author/degree]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("author"))),
                                stepExpr(new ChildAxisResolver(new QName("degree"))))))), namespaceContext),
                Arguments.of("book[degree][award]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("degree"))))),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("award"))))))),
                        namespaceContext),
                Arguments.of("author[last-name = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("last-name")))),
                                new LiteralExpr("Bob"))))), namespaceContext),
                Arguments.of("degree[@from != \"Harvard\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("degree")), new PredicateExpr(new NotEqualsExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("from")))),
                                new LiteralExpr("Harvard"))))), namespaceContext),
                Arguments.of("author[. = \"Matthew Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new SelfAxisResolver(ANY))), new LiteralExpr("Matthew Bob"))))),
                        namespaceContext),
                Arguments.of("author[last-name[1] = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("last-name")),
                                        new PredicateExpr(new NumberExpr(1.0)))), new LiteralExpr("Bob"))))),
                        namespaceContext),
                Arguments.of("author[* = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("*")))),
                                new LiteralExpr("Bob"))))), namespaceContext),
                Arguments.of("ancestor::book[1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("book"), false),
                                new PredicateExpr(new NumberExpr(1.0)))), namespaceContext),
                Arguments.of("ancestor::book[author][1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("book"), false),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("author"))))),
                                new PredicateExpr(new NumberExpr(1.0)))), namespaceContext),
                Arguments.of("ancestor::author[parent::book][1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("author"), false),
                                new PredicateExpr(pathExpr(stepExpr(new ParentAxisResolver(new QName("book"))))),
                                new PredicateExpr(new NumberExpr(1.0)))), namespaceContext)
        ));
    }

    private static Stream<Arguments> positivePrefixedNoContext() {
        return Stream.of(
                Arguments.of("my:book", pathExpr(stepExpr(new ChildAxisResolver(new QName("book"))))),
                Arguments.of("my:*", pathExpr(stepExpr(new ChildAxisResolver(new QName("*"))))),
                Arguments.of("@my:*", pathExpr(stepExpr(new AttributeAxisResolver(new QName("*")))))
        );
    }

    private static Stream<Arguments> positivePrefixedWithContext() {
        return Stream.of(
                Arguments.of("my:book", pathExpr(stepExpr(new ChildAxisResolver(
                        new QName("http://www.example.com/my", "book", "my"))))),
                Arguments.of("my:*", pathExpr(stepExpr(new ChildAxisResolver(
                        new QName("http://www.example.com/my", "*", "my"))))),
                Arguments.of("@my:*", pathExpr(stepExpr(new AttributeAxisResolver(
                        new QName("http://www.example.com/my", "*", "my")))))
        );
    }

    private static Stream<Arguments> negative() {
        return namespaceContexts().flatMap(namespaceContext -> Stream.of(
                Arguments.of("", namespaceContext),
                Arguments.of("...", namespaceContext),
                Arguments.of("//", namespaceContext),
                Arguments.of("///", namespaceContext),
                Arguments.of("my:book:com", namespaceContext),
                Arguments.of("bo@k", namespaceContext),
                Arguments.of("book[]", namespaceContext),
                Arguments.of("book[]]", namespaceContext),
                Arguments.of("book[[]", namespaceContext),
                Arguments.of("book[@style='value\"]", namespaceContext)
        ));
    }

    @ParameterizedTest(name = "Given simple XPath {0} should parse it into {1} using context {2}")
    @DisplayName("Should parse XPath using context")
    @MethodSource("positiveSimple")
    void shouldParseSimpleXPath(String xpath, Expr expectedExpr, NamespaceContext context)
            throws XPathExpressionException {
        Expr actualExpr = new XPathParser(context).parse(xpath);
        assertThat(actualExpr).hasToString(expectedExpr.toString());
    }

    @ParameterizedTest(name = "Given prefixed XPath {0} and no context should parse it into {1}")
    @DisplayName("Should parse prefixed XPath")
    @MethodSource("positivePrefixedNoContext")
    void shouldParsePrefixedXPathWithNoContext(String xpath, Expr expectedExpr) throws XPathExpressionException {
        Expr actualExpr = new XPathParser(null).parse(xpath);
        assertThat(actualExpr).hasToString(expectedExpr.toString());
    }

    @ParameterizedTest(name = "Given prefixed XPath {0} and some context should parse it into {1}")
    @DisplayName("Should parse prefixed XPath")
    @MethodSource("positivePrefixedWithContext")
    void shouldParsePrefixedXPathWithContext(String xpath, Expr expectedExpr) throws XPathExpressionException {
        Expr actualExpr = new XPathParser(new SimpleNamespaceContext()).parse(xpath);
        assertThat(actualExpr).hasToString(expectedExpr.toString());
    }

    @ParameterizedTest(name = "Given invalid XPath {0} should fail to parse it using context {1}")
    @DisplayName("Should fail to parse malformed XPath using context")
    @MethodSource("negative")
    void shouldThrowExceptionOnParse(final String invalidXPath, final NamespaceContext namespaceContext) {
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