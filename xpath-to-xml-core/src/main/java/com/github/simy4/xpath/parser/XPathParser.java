package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.expr.AdditionExpr;
import com.github.simy4.xpath.expr.Attribute;
import com.github.simy4.xpath.expr.DescendantOrSelfExpr;
import com.github.simy4.xpath.expr.Element;
import com.github.simy4.xpath.expr.EqualsExpr;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.GreaterThanExpr;
import com.github.simy4.xpath.expr.GreaterThanOrEqualsExpr;
import com.github.simy4.xpath.expr.Identity;
import com.github.simy4.xpath.expr.LessThanExpr;
import com.github.simy4.xpath.expr.LessThanOrEqualsExpr;
import com.github.simy4.xpath.expr.LiteralExpr;
import com.github.simy4.xpath.expr.MultiplicationExpr;
import com.github.simy4.xpath.expr.NotEqualsExpr;
import com.github.simy4.xpath.expr.NumberExpr;
import com.github.simy4.xpath.expr.Parent;
import com.github.simy4.xpath.expr.PathExpr;
import com.github.simy4.xpath.expr.PredicateExpr;
import com.github.simy4.xpath.expr.Root;
import com.github.simy4.xpath.expr.StepExpr;
import com.github.simy4.xpath.expr.SubtractionExpr;
import com.github.simy4.xpath.expr.UnaryExpr;
import com.github.simy4.xpath.parser.Token.Type;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * XPath parser.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@SuppressWarnings("MethodName")
public class XPathParser {

    private final NamespaceContext namespaceContext;

    public XPathParser(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    /**
     * Parse given XPath into {@link Expr} instance.
     *
     * @param xpath XPath to parse
     * @return resulting {@link Expr}
     * @throws XPathExpressionException if xpath cannot be parsed
     */
    public Expr parse(String xpath) throws XPathExpressionException {
        final Context context = new Context(xpath);
        final Expr expr = Expr(context);
        if (context.hasMoreElements()) {
            throw new XPathParserException(context.tokenAt(1));
        }
        return expr;
    }

    private Expr Expr(Context context) throws XPathExpressionException {
        return ComparisonExpr(context);
    }

    private Expr ComparisonExpr(Context context) throws XPathExpressionException {
        final Expr left = AdditiveExpr(context);
        final Expr right;
        switch (context.tokenAt(1).getType()) {
            case EQUALS:
                context.match(Type.EQUALS);
                right = AdditiveExpr(context);
                return new EqualsExpr(left, right);
            case NOT_EQUALS:
                context.match(Type.NOT_EQUALS);
                right = AdditiveExpr(context);
                return new NotEqualsExpr(left, right);
            case LESS_THAN_OR_EQUALS:
                context.match(Type.LESS_THAN_OR_EQUALS);
                right = AdditiveExpr(context);
                return new LessThanOrEqualsExpr(left, right);
            case LESS_THAN:
                context.match(Type.LESS_THAN);
                right = AdditiveExpr(context);
                return new LessThanExpr(left, right);
            case GREATER_THAN_OR_EQUALS:
                context.match(Type.GREATER_THAN_OR_EQUALS);
                right = AdditiveExpr(context);
                return new GreaterThanOrEqualsExpr(left, right);
            case GREATER_THAN:
                context.match(Type.GREATER_THAN);
                right = AdditiveExpr(context);
                return new GreaterThanExpr(left, right);
            default:
                return left;
        }
    }

    private Expr AdditiveExpr(Context context) throws XPathExpressionException {
        Expr left = MultiplicativeExpr(context);
        Type type = context.tokenAt(1).getType();
        while (Type.PLUS == type || Type.MINUS == type) {
            Expr right;
            switch (type) {
                case PLUS:
                    context.match(Type.PLUS);
                    right = MultiplicativeExpr(context);
                    left = new AdditionExpr(left, right);
                    break;
                case MINUS:
                    context.match(Type.MINUS);
                    right = MultiplicativeExpr(context);
                    left = new SubtractionExpr(left, right);
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.PLUS, Type.MINUS);
            }
            type = context.tokenAt(1).getType();
        }
        return left;
    }

    private Expr MultiplicativeExpr(Context context) throws XPathExpressionException {
        Expr left = UnaryExpr(context);
        Type type = context.tokenAt(1).getType();
        while (Type.STAR == type) {
            Expr right;
            switch (type) {
                case STAR:
                    context.match(Type.STAR);
                    right = UnaryExpr(context);
                    left = new MultiplicationExpr(left, right);
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.STAR);
            }
            type = context.tokenAt(1).getType();
        }
        return left;
    }

    private Expr UnaryExpr(Context context) throws XPathExpressionException {
        switch (context.tokenAt(1).getType()) {
            case MINUS:
                context.match(Type.MINUS);
                return new UnaryExpr(UnaryExpr(context));
            default:
                return ValueExpr(context);
        }
    }

    private Expr ValueExpr(Context context) throws XPathExpressionException {
        final String token;
        switch (context.tokenAt(1).getType()) {
            case LITERAL:
                token = context.match(Type.LITERAL).getToken();
                return new LiteralExpr(token);
            case DOUBLE:
                token = context.match(Type.DOUBLE).getToken();
                return new NumberExpr(Double.parseDouble(token));
            default:
                return PathExpr(context);
        }
    }

    private Expr PathExpr(Context context) throws XPathExpressionException {
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
                    default:
                }
                break;
            case DOUBLE_SLASH:
                context.match(Type.DOUBLE_SLASH);
                pathExpr.add(new Root());
                pathExpr.add(new DescendantOrSelfExpr());
                RelativePathExpr(context, pathExpr);
                break;
            default:
                RelativePathExpr(context, pathExpr);
                break;
        }
        return new PathExpr(pathExpr);
    }

    private void RelativePathExpr(Context context, List<StepExpr> pathExpr) throws XPathExpressionException {
        pathExpr.add(StepExpr(context));
        Type type = context.tokenAt(1).getType();
        while (Type.SLASH == type || Type.DOUBLE_SLASH == type) {
            switch (type) {
                case SLASH:
                    context.match(Type.SLASH);
                    pathExpr.add(StepExpr(context));
                    break;
                case DOUBLE_SLASH:
                    context.match(Type.DOUBLE_SLASH);
                    pathExpr.add(new DescendantOrSelfExpr());
                    pathExpr.add(StepExpr(context));
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.SLASH, Type.DOUBLE_SLASH);
            }
            type = context.tokenAt(1).getType();
        }
    }

    private StepExpr StepExpr(Context context) throws XPathExpressionException {
        final QName nodeTest;
        final List<Expr> predicateList;
        final StepExpr stepExpr;
        switch (context.tokenAt(1).getType()) {
            case DOT:
                context.match(Type.DOT);
                predicateList = PredicateList(context);
                stepExpr = new Identity(predicateList);
                break;
            case DOUBLE_DOT:
                context.match(Type.DOUBLE_DOT);
                predicateList = PredicateList(context);
                stepExpr = new Parent(predicateList);
                break;
            case AT:
                context.match(Type.AT);
                nodeTest = NodeTest(context);
                predicateList = PredicateList(context);
                stepExpr = new Attribute(nodeTest, predicateList);
                break;
            case STAR:
            case IDENTIFIER:
                nodeTest = NodeTest(context);
                predicateList = PredicateList(context);
                stepExpr = new Element(nodeTest, predicateList);
                break;
            default:
                throw new XPathParserException(context.tokenAt(1), Type.DOT, Type.DOUBLE_DOT, Type.AT, Type.STAR,
                        Type.IDENTIFIER);
        }
        return stepExpr;
    }

    private QName NodeTest(Context context) throws XPathExpressionException {
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
                    final String prefix;
                    final String namespaceUri;
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
                        default:
                    }
                } else {
                    return new QName(identifier.getToken());
                }
                // fallthrough
            default:
                throw new XPathParserException(context.tokenAt(1), Type.STAR, Type.IDENTIFIER);
        }
    }

    private List<Expr> PredicateList(Context context) throws XPathExpressionException {
        if (Type.LEFT_BRACKET == context.tokenAt(1).getType()) {
            final List<Expr> predicateList = new ArrayList<Expr>();
            predicateList.add(Predicate(context));
            while (Type.LEFT_BRACKET == context.tokenAt(1).getType()) {
                predicateList.add(Predicate(context));
            }
            return predicateList;
        } else {
            return Collections.emptyList();
        }
    }

    private PredicateExpr Predicate(Context context) throws XPathExpressionException {
        context.match(Type.LEFT_BRACKET);
        Expr predicate = Expr(context);
        context.match(Type.RIGHT_BRACKET);
        return new PredicateExpr(predicate);
    }

    private static final class Context {

        private final XPathLexer lexer;
        private final List<Token> tokens = new ArrayList<Token>();

        private Context(String xpath) {
            this.lexer = new XPathLexer(xpath);
        }

        private boolean hasMoreElements() {
            return tokenAt(1).getType() != Type.EOF;
        }

        private Token tokenAt(int i) {
            if (tokens.size() <= i - 1) {
                for (int j = 0; j < i; ++j) {
                    tokens.add(lexer.next());
                }
            }
            return tokens.get(i - 1);
        }

        private Token match(Type type) throws XPathExpressionException {
            final Token token = tokenAt(1);
            if (token.getType() == type) {
                tokens.remove(0);
                return token;
            }
            throw new XPathParserException(token, type);
        }
    }

}
