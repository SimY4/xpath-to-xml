package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.expr.Attribute;
import com.github.simy4.xpath.expr.ComparisonExpr;
import com.github.simy4.xpath.expr.Element;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.Identity;
import com.github.simy4.xpath.expr.MetaStepExpr;
import com.github.simy4.xpath.expr.NumberExpr;
import com.github.simy4.xpath.expr.Op;
import com.github.simy4.xpath.expr.PathExpr;
import com.github.simy4.xpath.expr.Root;
import com.github.simy4.xpath.expr.StepExpr;
import com.github.simy4.xpath.utils.Pair;
import com.github.simy4.xpath.utils.SimpleNamespaceContext;
import com.github.simy4.xpath.utils.Triple;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Theories.class)
public class XPathParserTest {

    @DataPoints("namespaceContexts")
    public static final NamespaceContext[] NAMESPACE_CONTEXTS = new NamespaceContext[] {
            null,
            new SimpleNamespaceContext(),
    };

    @DataPoints("Positive-Simple")
    public static Pair[] positiveSimple() {
        return new Pair[] {
                Pair.of("./author", pathExpr(stepExpr(new Identity()),
                        stepExpr(new Element(new QName("author"))))),
                Pair.of("author", pathExpr(stepExpr(new Element(new QName("author"))))),
                Pair.of("first.name", pathExpr(stepExpr(new Element(new QName("first.name"))))),
                Pair.of("/bookstore", pathExpr(new Root(), stepExpr(new Element(new QName("bookstore"))))),
                Pair.of("//author", pathExpr(new Root(), new Element(new QName("*")),
                        stepExpr(new Element(new QName("author"))))),
                Pair.of("book[/bookstore/@specialty=@style]",
                        pathExpr(stepExpr(new Element(new QName("book")), new ComparisonExpr(
                                pathExpr(
                                        new Root(),
                                        stepExpr(new Element(new QName("bookstore"))),
                                        stepExpr(new Attribute(new QName("specialty")))),
                                pathExpr(
                                        stepExpr(new Attribute(new QName("style")))),
                                Op.EQ)))),
                Pair.of("author/first-name", pathExpr(stepExpr(new Element(new QName("author"))),
                        stepExpr(new Element(new QName("first-name"))))),
                Pair.of("bookstore//title", pathExpr(
                        stepExpr(new Element(new QName("bookstore"))),
                        new Element(new QName("*")),
                        stepExpr(new Element(new QName("title"))))),
                Pair.of("bookstore/*/title", pathExpr(stepExpr(new Element(new QName("bookstore"))),
                        stepExpr(new Element(new QName("*"))),
                        stepExpr(new Element(new QName("title"))))),
                Pair.of("bookstore//book/excerpt//emph", pathExpr(stepExpr(new Element(new QName("bookstore"))),
                        new Element(new QName("*")),
                        stepExpr(new Element(new QName("book"))),
                        stepExpr(new Element(new QName("excerpt"))),
                        new Element(new QName("*")),
                        stepExpr(new Element(new QName("emph"))))),
                Pair.of(".//title", pathExpr(stepExpr(new Identity()), new Element(new QName("*")),
                        stepExpr(new Element(new QName("title"))))),
                Pair.of("author/*", pathExpr(stepExpr(new Element(new QName("author"))),
                        stepExpr(new Element(new QName("*"))))),
                Pair.of("book/*/last-name", pathExpr(stepExpr(new Element(new QName("book"))),
                        stepExpr(new Element(new QName("*"))),
                        stepExpr(new Element(new QName("last-name"))))),
                Pair.of("*/*", pathExpr(stepExpr(new Element(new QName("*"))),
                        stepExpr(new Element(new QName("*"))))),
                Pair.of("*[@specialty]", pathExpr(stepExpr(new Element(new QName("*")),
                        pathExpr(stepExpr(new Attribute(new QName("specialty"))))))),
                Pair.of("@style", pathExpr(stepExpr(new Attribute(new QName("style"))))),
                Pair.of("price/@exchange", pathExpr(stepExpr(new Element(new QName("price"))),
                        stepExpr(new Attribute(new QName("exchange"))))),
                Pair.of("price/@exchange/total", pathExpr(stepExpr(new Element(new QName("price"))),
                        stepExpr(new Attribute(new QName("exchange"))),
                        stepExpr(new Element(new QName("total"))))),
                Pair.of("book[@style]", pathExpr(stepExpr(new Element(new QName("book")),
                        pathExpr(stepExpr(new Attribute(new QName("style"))))))),
                Pair.of("book/@style", pathExpr(stepExpr(new Element(new QName("book"))),
                        stepExpr(new Attribute(new QName("style"))))),
                Pair.of("@*", pathExpr(stepExpr(new Attribute(new QName("*"))))),
                Pair.of("./first-name", pathExpr(stepExpr(new Identity()),
                        stepExpr(new Element(new QName("first-name"))))),
                Pair.of("first-name", pathExpr(stepExpr(new Element(new QName("first-name"))))),
                Pair.of("author[1]", pathExpr(stepExpr(new Element(new QName("author")), new NumberExpr(1.0)))),
                Pair.of("author[first-name][3]", pathExpr(stepExpr(new Element(new QName("author")),
                        pathExpr(stepExpr(new Element(new QName("first-name")))), new NumberExpr(3.0)))),
        };
    }

    @DataPoints("Positive-Prefixed")
    public static Triple[] positivePrefixed() {
        return new Triple[] {
                Triple.of("my:book", pathExpr(stepExpr(new Element(new QName("book")))),
                        pathExpr(stepExpr(new Element(new QName("http://www.example.com/my", "book", "my"))))),
                Triple.of("my:*", pathExpr(stepExpr(new Element(new QName("*")))),
                        pathExpr(stepExpr(new Element(new QName("http://www.example.com/my", "*", "my"))))),
                Triple.of("@my:*", pathExpr(stepExpr(new Attribute(new QName("*")))),
                        pathExpr(stepExpr(new Attribute(new QName("http://www.example.com/my", "*", "my"))))),
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
            throws XPathParserException {
        Expr actualExpr = new XPathParser(context).parse(data.getFirst());
        assertThat(actualExpr).hasToString(data.getSecond().toString());
    }

    @Theory
    public void shouldParsePrefixedXPath(@FromDataPoints("Positive-Prefixed") Triple<String, Expr, Expr> data,
                                         @FromDataPoints("namespaceContexts") NamespaceContext context)
            throws XPathParserException {
        Expr actualExpr = new XPathParser(context).parse(data.getFirst());
        assertThat(actualExpr).hasToString(null == context ? data.getSecond().toString() : data.getThird().toString());
    }

    @Theory
    public void shouldThrowExceptionOnParse(@FromDataPoints("Negative") String invalidXPath,
                                            @FromDataPoints("namespaceContexts") NamespaceContext namespaceContext) {
        try {
            System.err.println(new XPathParser(namespaceContext).parse(invalidXPath));
            fail("Should throw XPathParserException");
        } catch (XPathParserException ignored) { }
    }

    private static Expr pathExpr(StepExpr... pathExpr) {
        return new PathExpr(asList(pathExpr));
    }

    private static StepExpr stepExpr(StepExpr step, Expr... predicates) {
        return new MetaStepExpr(step, asList(predicates));
    }

}