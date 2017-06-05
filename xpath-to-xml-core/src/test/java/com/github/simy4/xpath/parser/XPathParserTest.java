package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.expr.Attribute;
import com.github.simy4.xpath.expr.ComparisonExpr;
import com.github.simy4.xpath.expr.Element;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.Identity;
import com.github.simy4.xpath.expr.NumberExpr;
import com.github.simy4.xpath.expr.Op;
import com.github.simy4.xpath.expr.PathExpr;
import com.github.simy4.xpath.expr.Root;
import com.github.simy4.xpath.expr.StepExpr;
import com.github.simy4.xpath.utils.Pair;
import com.github.simy4.xpath.utils.SimpleNamespaceContext;
import com.github.simy4.xpath.utils.Triple;
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
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Theories.class)
public class XPathParserTest {

    private static final List<Expr> NIL = Collections.emptyList();

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
                Pair.of("./author", pathExpr(new Identity(NIL), new Element(new QName("author"), NIL))),
                Pair.of("author", pathExpr(new Element(new QName("author"), NIL))),
                Pair.of("first.name", pathExpr(new Element(new QName("first.name"), NIL))),
                Pair.of("/bookstore", pathExpr(new Root(), new Element(new QName("bookstore"), NIL))),
                Pair.of("//author", pathExpr(new Root(), new Element(new QName("*"), NIL),
                        new Element(new QName("author"), NIL))),
                Pair.of("book[/bookstore/@specialty=@style]",
                        pathExpr(new Element(new QName("book"), Collections.<Expr>singletonList(
                                new ComparisonExpr(
                                        pathExpr(
                                                new Root(),
                                                new Element(new QName("bookstore"), NIL),
                                                new Attribute(new QName("specialty"), NIL)),
                                        pathExpr(
                                                new Attribute(new QName("style"), NIL)),
                                        Op.EQ))))),
                Pair.of("author/first-name", pathExpr(new Element(new QName("author"), NIL),
                        new Element(new QName("first-name"), NIL))),
                Pair.of("bookstore//title", pathExpr(
                        new Element(new QName("bookstore"), NIL),
                        new Element(new QName("*"), NIL),
                        new Element(new QName("title"), NIL))),
                Pair.of("bookstore/*/title", pathExpr(new Element(new QName("bookstore"), NIL),
                        new Element(new QName("*"), NIL),
                        new Element(new QName("title"), NIL))),
                Pair.of("bookstore//book/excerpt//emph", pathExpr(new Element(new QName("bookstore"), NIL),
                        new Element(new QName("*"), NIL),
                        new Element(new QName("book"), NIL),
                        new Element(new QName("excerpt"), NIL),
                        new Element(new QName("*"), NIL),
                        new Element(new QName("emph"), NIL))),
                Pair.of(".//title", pathExpr(new Identity(NIL), new Element(new QName("*"), NIL),
                        new Element(new QName("title"), NIL))),
                Pair.of("author/*", pathExpr(new Element(new QName("author"), NIL),
                        new Element(new QName("*"), NIL))),
                Pair.of("book/*/last-name", pathExpr(new Element(new QName("book"), NIL),
                        new Element(new QName("*"), NIL),
                        new Element(new QName("last-name"), NIL))),
                Pair.of("*/*", pathExpr(new Element(new QName("*"), NIL),
                        new Element(new QName("*"), NIL))),
                Pair.of("*[@specialty]", pathExpr(new Element(new QName("*"), singletonList(
                        pathExpr(new Attribute(new QName("specialty"), NIL)))))),
                Pair.of("@style", pathExpr(new Attribute(new QName("style"), NIL))),
                Pair.of("price/@exchange", pathExpr(new Element(new QName("price"), NIL),
                        new Attribute(new QName("exchange"), NIL))),
                Pair.of("price/@exchange/total", pathExpr(new Element(new QName("price"), NIL),
                        new Attribute(new QName("exchange"), NIL),
                        new Element(new QName("total"), NIL))),
                Pair.of("book[@style]", pathExpr(new Element(new QName("book"), singletonList(
                        pathExpr(new Attribute(new QName("style"), NIL)))))),
                Pair.of("book/@style", pathExpr(new Element(new QName("book"), NIL),
                        new Attribute(new QName("style"), NIL))),
                Pair.of("@*", pathExpr(new Attribute(new QName("*"), NIL))),
                Pair.of("./first-name", pathExpr(new Identity(NIL),
                        new Element(new QName("first-name"), NIL))),
                Pair.of("first-name", pathExpr(new Element(new QName("first-name"), NIL))),
                Pair.of("author[1]", pathExpr(new Element(new QName("author"),
                        Collections.<Expr>singletonList(new NumberExpr(1.0))))),
                Pair.of("author[first-name][3]", pathExpr(new Element(new QName("author"), asList(
                        pathExpr(new Element(new QName("first-name"), NIL)), new NumberExpr(3.0))))),
        };
    }

    @DataPoints("Positive-Prefixed")
    public static Triple[] positivePrefixed() {
        return new Triple[] {
                Triple.of("my:book", pathExpr(new Element(new QName("book"), NIL)),
                        pathExpr(new Element(new QName("http://www.example.com/my", "book", "my"), NIL))),
                Triple.of("my:*", pathExpr(new Element(new QName("*"), NIL)),
                        pathExpr(new Element(new QName("http://www.example.com/my", "*", "my"), NIL))),
                Triple.of("@my:*", pathExpr(new Attribute(new QName("*"), NIL)),
                        pathExpr(new Attribute(new QName("http://www.example.com/my", "*", "my"), NIL))),
        };
    }

    @DataPoints("Negative")
    public static final String[] INVALID_X_PATHS = new String[] {
            "...",
            "///",
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

    private static Expr pathExpr(StepExpr... pathExpr) {
        return new PathExpr(asList(pathExpr));
    }

}