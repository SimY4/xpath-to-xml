package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.expr.Attribute;
import com.github.simy4.xpath.expr.ComparisonExpr;
import com.github.simy4.xpath.expr.Element;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.Identity;
import com.github.simy4.xpath.expr.LiteralExpr;
import com.github.simy4.xpath.expr.MetaStepExpr;
import com.github.simy4.xpath.expr.NumberExpr;
import com.github.simy4.xpath.expr.Op;
import com.github.simy4.xpath.expr.Parent;
import com.github.simy4.xpath.expr.PathExpr;
import com.github.simy4.xpath.expr.Root;
import com.github.simy4.xpath.expr.StepExpr;
import com.github.simy4.xpath.parser.Token.Type;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * XPath parser.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@ThreadSafe
public class XPathParser {

    private final NamespaceContext namespaceContext;

    public XPathParser(@Nullable NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    /**
     * Parse given XPath into {@link Expr} instance.
     *
     * @param xpath XPath to parse
     * @return resulting {@link Expr}
     * @throws XPathParserException if xpath cannot be parsed
     */
    public Expr parse(String xpath) throws XPathParserException {
        final Context context = new Context(xpath);
        Expr expr = Expr(context);
        if (context.hasMoreElements()) {
            throw new XPathParserException(context.tokenAt(1));
        }
        return expr;
    }

    private Expr Expr(Context context) throws XPathParserException {
        return ComparisonExpr(context);
    }

    private Expr ComparisonExpr(Context context) throws XPathParserException {
        Expr left = ValueExpr(context);
        switch (context.tokenAt(1).getType()) {
            case EQUALS:
                context.match(Type.EQUALS);
                Expr right = ValueExpr(context);
                return new ComparisonExpr(left, right, Op.EQ);
            default:
                return left;
        }
    }

    private Expr ValueExpr(Context context) throws XPathParserException {
        switch (context.tokenAt(1).getType()) {
            case LITERAL:
                String token = context.match(Type.LITERAL).getToken();
                return new LiteralExpr(token);
            case DOUBLE:
                double d = Double.parseDouble(context.match(Type.DOUBLE).getToken());
                return new NumberExpr(d);
            default:
                return PathExpr(context);
        }
    }

    private Expr PathExpr(Context context) throws XPathParserException {
        final List<StepExpr> pathExpr = new ArrayList<StepExpr>();
        switch (context.tokenAt(1).getType()) {
            case SLASH:
                context.match(Type.SLASH);
                pathExpr.add(new Root());
                switch (context.tokenAt(1).getType()) {
                    case DOT:
                    case DOUBLE_DOT:
                    case AT:
                    case STAR:
                    case IDENTIFIER:
                        RelativePathExpr(context, pathExpr);
                        break;
                }
                break;
            case DOUBLE_SLASH:
                context.match(Type.DOUBLE_SLASH);
                pathExpr.add(new Root());
                pathExpr.add(new Element(new QName("*"))); // TODO
                RelativePathExpr(context, pathExpr);
                break;
            default:
                RelativePathExpr(context, pathExpr);
                break;
        }
        return new PathExpr(pathExpr);
    }

    private void RelativePathExpr(Context context, List<StepExpr> pathExpr) throws XPathParserException {
        pathExpr.add(StepExpr(context));
        Type type = context.tokenAt(1).getType();
        while (Type.SLASH == type || Type.DOUBLE_SLASH == type) {
            switch (context.tokenAt(1).getType()) {
                case SLASH:
                    context.match(Type.SLASH);
                    pathExpr.add(StepExpr(context));
                    break;
                case DOUBLE_SLASH:
                    context.match(Type.DOUBLE_SLASH);
                    pathExpr.add(new Element(new QName("*"))); // TODO
                    pathExpr.add(StepExpr(context));
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.SLASH, Type.DOUBLE_SLASH);
            }
            type = context.tokenAt(1).getType();
        }
    }

    private StepExpr StepExpr(Context context) throws XPathParserException {
        final StepExpr stepExpr;
        switch (context.tokenAt(1).getType()) {
            case DOT:
                context.match(Type.DOT);
                stepExpr = new Identity();
                break;
            case DOUBLE_DOT:
                context.match(Type.DOUBLE_DOT);
                stepExpr = new Parent();
                break;
            case AT:
                context.match(Type.AT);
                stepExpr = new Attribute(NodeTest(context));
                break;
            case STAR:
            case IDENTIFIER:
                stepExpr = new Element(NodeTest(context));
                break;
            default:
                throw new XPathParserException(context.tokenAt(1), Type.DOT, Type.DOUBLE_DOT, Type.AT, Type.STAR,
                        Type.IDENTIFIER);
        }
        List<Expr> predicateList = PredicateList(context);
        return new MetaStepExpr(stepExpr, predicateList);
    }

    private QName NodeTest(Context context) throws XPathParserException {
        switch (context.tokenAt(1).getType()) {
            case STAR:
                final Token star = context.match(Type.STAR);
                if (Type.COLON == context.tokenAt(1).getType()) {
                    context.match(Type.COLON);
                    return new QName(star.getToken(), context.match(Type.IDENTIFIER).getToken());
                } else {
                    return new QName(star.getToken());
                }
            case IDENTIFIER:
                final Token identifier = context.match(Type.IDENTIFIER);
                if (Type.COLON == context.tokenAt(1).getType()) {
                    context.match(Type.COLON);
                    final String prefix, namespaceUri;
                    if (null == namespaceContext) {
                        prefix = XMLConstants.DEFAULT_NS_PREFIX;
                        namespaceUri = XMLConstants.NULL_NS_URI;
                    } else {
                        prefix = identifier.getToken();
                        namespaceUri = namespaceContext.getNamespaceURI(prefix);
                    }
                    switch (context.tokenAt(1).getType()) {
                        case STAR:
                            return new QName(namespaceUri, context.match(Type.STAR).getToken(), prefix);
                        case IDENTIFIER:
                            return new QName(namespaceUri, context.match(Type.IDENTIFIER).getToken(), prefix);
                    }
                } else {
                    return new QName(identifier.getToken());
                }
            default:
                throw new XPathParserException(context.tokenAt(1), Type.STAR, Type.IDENTIFIER);
        }
    }

    private List<Expr> PredicateList(Context context) throws XPathParserException {
        final List<Expr> predicateList = new ArrayList<Expr>();
        Type type = context.tokenAt(1).getType();
        while (Type.LEFT_BRACKET == type) {
            context.match(Type.LEFT_BRACKET);
            predicateList.add(Predicate(context));
            context.match(Type.RIGHT_BRACKET);
            type = context.tokenAt(1).getType();
        }
        return predicateList;
    }

    private Expr Predicate(Context context) throws XPathParserException {
        return Expr(context);
    }

    private static class Context {
        private final XPathLexer lexer;
        private final List<Token> tokens = new ArrayList<Token>();

        Context(String xpath) {
            this.lexer = new XPathLexer(xpath);
        }

        boolean hasMoreElements() {
            return tokenAt(1).getType() != Type.EOF;
        }

        Token tokenAt(@Nonnegative int i) {
            if (tokens.size() <= i - 1) {
                for (int j = 0; j < i; ++j) {
                    tokens.add(lexer.next());
                }
            }
            return tokens.get(i - 1);
        }

        Token match(Type type) throws XPathParserException {
            final Token token = tokenAt(1);
            if (token.getType() == type) {
                tokens.remove(0);
                return token;
            }
            throw new XPathParserException(token, type);
        }
    }

}
