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
import com.github.simy4.xpath.util.Pair;
import com.github.simy4.xpath.util.SimpleNamespaceContext;
import com.github.simy4.xpath.util.Triple;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class XPathParserTest {

    private static final QName ANY = new QName("*", "*");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DataPoints("namespaceContexts")
    public static final NamespaceContext[] NAMESPACE_CONTEXTS = new NamespaceContext[] {
            null,
            new SimpleNamespaceContext(),
    };

    @DataPoints("Positive-Simple")
    public static Pair[] positiveSimple() {
        return new Pair[] {
                Pair.of("./author", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new ChildAxisResolver(new QName("author"))))),
                Pair.of("author", pathExpr(stepExpr(new ChildAxisResolver(new QName("author"))))),
                Pair.of("first.name", pathExpr(stepExpr(new ChildAxisResolver(new QName("first.name"))))),
                Pair.of("/bookstore", pathExpr(
                        new Root(),
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))))),
                Pair.of("//author", pathExpr(
                        new Root(),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("author"))))),
                Pair.of("book[/bookstore/@specialty=@style]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(new EqualsExpr(
                                pathExpr(
                                        new Root(),
                                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                                        stepExpr(new AttributeAxisResolver(new QName("specialty")))),
                                pathExpr(
                                        stepExpr(new AttributeAxisResolver(new QName("style"))))))))),
                Pair.of("author/first-name", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author"))),
                        stepExpr(new ChildAxisResolver(new QName("first-name"))))),
                Pair.of("bookstore//title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("title"))))),
                Pair.of("bookstore/*/title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("title"))))),
                Pair.of("bookstore//book/excerpt//emph", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("bookstore"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new ChildAxisResolver(new QName("excerpt"))),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("emph"))))),
                Pair.of(".//title", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new DescendantOrSelfAxisResolver(ANY, true)),
                        stepExpr(new ChildAxisResolver(new QName("title"))))),
                Pair.of("author/*", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))))),
                Pair.of("book/*/last-name", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("last-name"))))),
                Pair.of("*/*", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("*"))),
                        stepExpr(new ChildAxisResolver(new QName("*"))))),
                Pair.of("*[@specialty]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("*")), new PredicateExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("specialty")))))))),
                Pair.of("@style", pathExpr(stepExpr(new AttributeAxisResolver(new QName("style"))))),
                Pair.of("price/@exchange", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("price"))),
                        stepExpr(new AttributeAxisResolver(new QName("exchange"))))),
                Pair.of("price/@exchange/total", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("price"))),
                        stepExpr(new AttributeAxisResolver(new QName("exchange"))),
                        stepExpr(new ChildAxisResolver(new QName("total"))))),
                Pair.of("book[@style]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("style")))))))),
                Pair.of("book/@style", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book"))),
                        stepExpr(new AttributeAxisResolver(new QName("style"))))),
                Pair.of("@*", pathExpr(stepExpr(new AttributeAxisResolver(new QName("*"))))),
                Pair.of("./first-name", pathExpr(
                        stepExpr(new SelfAxisResolver(ANY)),
                        stepExpr(new ChildAxisResolver(new QName("first-name"))))),
                Pair.of("first-name", pathExpr(stepExpr(new ChildAxisResolver(new QName("first-name"))))),
                Pair.of("author[1]", pathExpr(stepExpr(new ChildAxisResolver(new QName("author")),
                        new PredicateExpr(new NumberExpr(1.0))))),
                Pair.of("author[first-name][3]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")),
                                new PredicateExpr(pathExpr(
                                        stepExpr(new ChildAxisResolver(new QName("first-name"))))),
                                new PredicateExpr(new NumberExpr(3.0))))),
                Pair.of("1 + 2 + 2 * 2 - -4", new MultiplicationExpr(new AdditionExpr(new NumberExpr(1.0),
                        new AdditionExpr(new NumberExpr(2.0), new NumberExpr(2.0))),
                        new SubtractionExpr(new NumberExpr(2.0), new UnaryExpr(new NumberExpr(4.0))))),
                Pair.of("book[excerpt]", pathExpr(stepExpr(new ChildAxisResolver(new QName("book")),
                        new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("excerpt")))))))),
                Pair.of("book[excerpt]/title", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("excerpt")))))),
                        stepExpr(new ChildAxisResolver(new QName("title"))))),
                Pair.of("book[excerpt]/author[degree]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("excerpt")))))),
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("degree")))))))),
                Pair.of("book[author/degree]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")), new PredicateExpr(pathExpr(
                                stepExpr(new ChildAxisResolver(new QName("author"))),
                                stepExpr(new ChildAxisResolver(new QName("degree")))))))),
                Pair.of("book[degree][award]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("book")),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("degree"))))),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("award")))))))),
                Pair.of("author[last-name = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("last-name")))),
                                new LiteralExpr("Bob")))))),
                Pair.of("degree[@from != \"Harvard\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("degree")), new PredicateExpr(new NotEqualsExpr(
                                pathExpr(stepExpr(new AttributeAxisResolver(new QName("from")))),
                                new LiteralExpr("Harvard")))))),
                Pair.of("author[. = \"Matthew Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new SelfAxisResolver(ANY))), new LiteralExpr("Matthew Bob")))))),
                Pair.of("author[last-name[1] = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("last-name")),
                                        new PredicateExpr(new NumberExpr(1.0)))), new LiteralExpr("Bob")))))),
                Pair.of("author[* = \"Bob\"]", pathExpr(
                        stepExpr(new ChildAxisResolver(new QName("author")), new PredicateExpr(new EqualsExpr(
                                pathExpr(stepExpr(new ChildAxisResolver(new QName("*")))),
                                new LiteralExpr("Bob")))))),
                Pair.of("ancestor::book[1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("book"), false),
                                new PredicateExpr(new NumberExpr(1.0))))),
                Pair.of("ancestor::book[author][1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("book"), false),
                                new PredicateExpr(pathExpr(stepExpr(new ChildAxisResolver(new QName("author"))))),
                                new PredicateExpr(new NumberExpr(1.0))))),
                Pair.of("ancestor::author[parent::book][1]", pathExpr(
                        stepExpr(new AncestorOrSelfAxisResolver(new QName("author"), false),
                                new PredicateExpr(pathExpr(stepExpr(new ParentAxisResolver(new QName("book"))))),
                                new PredicateExpr(new NumberExpr(1.0))))),
        };
    }

    @DataPoints("Positive-Prefixed")
    public static Triple[] positivePrefixed() {
        return new Triple[] {
                Triple.of("my:book", pathExpr(stepExpr(new ChildAxisResolver(new QName("book")))),
                        pathExpr(stepExpr(new ChildAxisResolver(new QName("http://www.example.com/my", "book", "my"))))),
                Triple.of("my:*", pathExpr(stepExpr(new ChildAxisResolver(new QName("*")))),
                        pathExpr(stepExpr(new ChildAxisResolver(new QName("http://www.example.com/my", "*", "my"))))),
                Triple.of("@my:*", pathExpr(stepExpr(new AttributeAxisResolver(new QName("*")))),
                        pathExpr(stepExpr(new AttributeAxisResolver(new QName("http://www.example.com/my", "*", "my"))))),
        };
    }

    @DataPoints("Negative")
    public static final String[] INVALID_X_PATHS = new String[] {
            "",
            "...",
            "//",
            "///",
            "my:book:com",
            "bo@k",
            "book[]",
            "book[]]",
            "book[[]",
            "book[@style='value\"]",
    };

    @Theory
    public void shouldParseSimpleXPath(@FromDataPoints("Positive-Simple") Pair<String, Expr> data,
                                       @FromDataPoints("namespaceContexts") NamespaceContext context)
            throws XPathExpressionException {
        Expr actualExpr = new XPathParser(context).parse(data.getFirst());
        assertThat(actualExpr).hasToString(data.getSecond().toString());
    }

    @Theory
    public void shouldParsePrefixedXPath(@FromDataPoints("Positive-Prefixed") Triple<String, Expr, Expr> data,
                                         @FromDataPoints("namespaceContexts") NamespaceContext context)
            throws XPathExpressionException {
        Expr actualExpr = new XPathParser(context).parse(data.getFirst());
        assertThat(actualExpr).hasToString(null == context ? data.getSecond().toString() : data.getThird().toString());
    }

    @Theory
    public void shouldThrowExceptionOnParse(@FromDataPoints("Negative") String invalidXPath,
                                            @FromDataPoints("namespaceContexts") NamespaceContext namespaceContext)
            throws XPathExpressionException {
        thrown.expect(XPathExpressionException.class);
        System.err.println(new XPathParser(namespaceContext).parse(invalidXPath));
    }

    private static Expr pathExpr(StepExpr... steps) {
        return new PathExpr(asList(steps));
    }

    private static StepExpr stepExpr(AxisResolver axisResolver, Expr... predicates) {
        return new AxisStepExpr(axisResolver, asList(predicates));
    }

}