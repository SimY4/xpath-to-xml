package com.github.simy4.xpath.parser;

import com.github.simy4.xpath.expr.AdditionExpr;
import com.github.simy4.xpath.expr.AxisStepExpr;
import com.github.simy4.xpath.expr.EqualsExpr;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.expr.GreaterThanExpr;
import com.github.simy4.xpath.expr.GreaterThanOrEqualsExpr;
import com.github.simy4.xpath.expr.LessThanExpr;
import com.github.simy4.xpath.expr.LessThanOrEqualsExpr;
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
import com.github.simy4.xpath.parser.Token.Type;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XPath parser.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@SuppressWarnings({"MethodName", "SwitchStatementWithTooFewBranches"})
public class XPathParser implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final QName ANY = new QName("*", "*");

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
            case Type.EQUALS:
                context.match(Type.EQUALS);
                right = AdditiveExpr(context);
                return new EqualsExpr(left, right);
            case Type.NOT_EQUALS:
                context.match(Type.NOT_EQUALS);
                right = AdditiveExpr(context);
                return new NotEqualsExpr(left, right);
            case Type.LESS_THAN_OR_EQUALS:
                context.match(Type.LESS_THAN_OR_EQUALS);
                right = AdditiveExpr(context);
                return new LessThanOrEqualsExpr(left, right);
            case Type.LESS_THAN:
                context.match(Type.LESS_THAN);
                right = AdditiveExpr(context);
                return new LessThanExpr(left, right);
            case Type.GREATER_THAN_OR_EQUALS:
                context.match(Type.GREATER_THAN_OR_EQUALS);
                right = AdditiveExpr(context);
                return new GreaterThanOrEqualsExpr(left, right);
            case Type.GREATER_THAN:
                context.match(Type.GREATER_THAN);
                right = AdditiveExpr(context);
                return new GreaterThanExpr(left, right);
            default:
                return left;
        }
    }

    private Expr AdditiveExpr(Context context) throws XPathExpressionException {
        Expr left = MultiplicativeExpr(context);
        short type = context.tokenAt(1).getType();
        while (Type.PLUS == type || Type.MINUS == type) {
            Expr right;
            switch (type) {
                case Type.PLUS:
                    context.match(Type.PLUS);
                    right = MultiplicativeExpr(context);
                    left = new AdditionExpr(left, right);
                    break;
                case Type.MINUS:
                    context.match(Type.MINUS);
                    right = MultiplicativeExpr(context);
                    left = new SubtractionExpr(left, right);
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.lookup(Type.PLUS, Type.MINUS));
            }
            type = context.tokenAt(1).getType();
        }
        return left;
    }

    private Expr MultiplicativeExpr(Context context) throws XPathExpressionException {
        Expr left = UnaryExpr(context);
        short type = context.tokenAt(1).getType();
        while (Type.STAR == type) {
            Expr right;
            switch (type) {
                case Type.STAR:
                    context.match(Type.STAR);
                    right = UnaryExpr(context);
                    left = new MultiplicationExpr(left, right);
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.lookup(Type.STAR));
            }
            type = context.tokenAt(1).getType();
        }
        return left;
    }

    private Expr UnaryExpr(Context context) throws XPathExpressionException {
        switch (context.tokenAt(1).getType()) {
            case Type.MINUS:
                context.match(Type.MINUS);
                return new UnaryExpr(UnaryExpr(context));
            default:
                return ValueExpr(context);
        }
    }

    private Expr ValueExpr(Context context) throws XPathExpressionException {
        final String token;
        switch (context.tokenAt(1).getType()) {
            case Type.LITERAL:
                token = context.match(Type.LITERAL).getToken();
                return new LiteralExpr(token);
            case Type.DOUBLE:
                token = context.match(Type.DOUBLE).getToken();
                return new NumberExpr(Double.parseDouble(token));
            default:
                return PathExpr(context);
        }
    }

    private Expr PathExpr(Context context) throws XPathExpressionException {
        final List<StepExpr> pathExpr = new ArrayList<StepExpr>();
        switch (context.tokenAt(1).getType()) {
            case Type.SLASH:
                context.match(Type.SLASH);
                pathExpr.add(new Root());
                switch (context.tokenAt(1).getType()) {
                    case Type.DOT:
                    case Type.DOUBLE_DOT:
                    case Type.AT:
                    case Type.STAR:
                    case Type.IDENTIFIER:
                        RelativePathExpr(context, pathExpr);
                        break;
                    default:
                }
                break;
            case Type.DOUBLE_SLASH:
                context.match(Type.DOUBLE_SLASH);
                pathExpr.add(new Root());
                pathExpr.add(new AxisStepExpr(new DescendantOrSelfAxisResolver(ANY, true),
                        Collections.<Expr>emptySet()));
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
        short type = context.tokenAt(1).getType();
        while (Type.SLASH == type || Type.DOUBLE_SLASH == type) {
            switch (type) {
                case Type.SLASH:
                    context.match(Type.SLASH);
                    pathExpr.add(StepExpr(context));
                    break;
                case Type.DOUBLE_SLASH:
                    context.match(Type.DOUBLE_SLASH);
                    pathExpr.add(new AxisStepExpr(new DescendantOrSelfAxisResolver(ANY, true),
                            Collections.<Expr>emptySet()));
                    pathExpr.add(StepExpr(context));
                    break;
                default:
                    throw new XPathParserException(context.tokenAt(1), Type.lookup(Type.SLASH, Type.DOUBLE_SLASH));
            }
            type = context.tokenAt(1).getType();
        }
    }

    private StepExpr StepExpr(Context context) throws XPathExpressionException {
        AxisResolver axisResolver;
        List<Expr> predicateList;
        switch (context.tokenAt(1).getType()) {
            case Type.DOT:
                context.match(Type.DOT);
                predicateList = PredicateList(context);
                return new AxisStepExpr(new SelfAxisResolver(ANY), predicateList);
            case Type.DOUBLE_DOT:
                context.match(Type.DOUBLE_DOT);
                predicateList = PredicateList(context);
                return new AxisStepExpr(new ParentAxisResolver(ANY), predicateList);
            case Type.AT:
                context.match(Type.AT);
                axisResolver = new AttributeAxisResolver(NodeTest(context));
                break;
            case Type.STAR:
            case Type.IDENTIFIER:
                if (Type.DOUBLE_COLON == context.tokenAt(2).getType()) {
                    axisResolver = AxisTest(context);
                } else {
                    axisResolver = new ChildAxisResolver(NodeTest(context));
                }
                break;
            default:
                throw new XPathParserException(context.tokenAt(1), Type.lookup(Type.DOT, Type.DOUBLE_DOT, Type.AT,
                        Type.STAR, Type.IDENTIFIER));
        }
        predicateList = PredicateList(context);
        return new AxisStepExpr(axisResolver, predicateList);
    }

    private AxisResolver AxisTest(Context context) throws XPathExpressionException {
        final Token axisToken = context.tokenAt(1);
        context.match(Type.IDENTIFIER);
        context.match(Type.DOUBLE_COLON);
        final AxisResolver axisResolver;
        switch (Axis.lookup(axisToken)) {
            case Axis.CHILD:
                axisResolver = new ChildAxisResolver(NodeTest(context));
                break;
            case Axis.DESCENDANT:
                axisResolver = new DescendantOrSelfAxisResolver(NodeTest(context), false);
                break;
            case Axis.PARENT:
                axisResolver = new ParentAxisResolver(NodeTest(context));
                break;
            case Axis.ANCESTOR:
                axisResolver = new AncestorOrSelfAxisResolver(NodeTest(context), false);
                break;
            case Axis.ATTRIBUTE:
                axisResolver = new AttributeAxisResolver(NodeTest(context));
                break;
            case Axis.SELF:
                axisResolver = new SelfAxisResolver(NodeTest(context));
                break;
            case Axis.DESCENDANT_OR_SELF:
                axisResolver = new DescendantOrSelfAxisResolver(NodeTest(context), true);
                break;
            case Axis.ANCESTOR_OR_SELF:
                axisResolver = new AncestorOrSelfAxisResolver(NodeTest(context), true);
                break;
            default:
                throw new XPathParserException(axisToken, Type.lookup(Type.IDENTIFIER));
        }
        return axisResolver;
    }

    @SuppressWarnings("fallthrough")
    private QName NodeTest(Context context) throws XPathExpressionException {
        switch (context.tokenAt(1).getType()) {
            case Type.STAR:
                final Token star = context.match(Type.STAR);
                if (Type.COLON == context.tokenAt(1).getType()) {
                    context.match(Type.COLON);
                    return new QName(star.getToken(), context.match(Type.IDENTIFIER).getToken());
                } else {
                    return new QName(star.getToken());
                }
            case Type.IDENTIFIER:
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
                        case Type.STAR:
                            return new QName(namespaceUri, context.match(Type.STAR).getToken(), prefix);
                        case Type.IDENTIFIER:
                            return new QName(namespaceUri, context.match(Type.IDENTIFIER).getToken(), prefix);
                        default:
                    }
                } else {
                    return new QName(identifier.getToken());
                }
                // fallthrough
            default:
                throw new XPathParserException(context.tokenAt(1), Type.lookup(Type.STAR, Type.IDENTIFIER));
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

    private Expr Predicate(Context context) throws XPathExpressionException {
        context.match(Type.LEFT_BRACKET);
        Expr predicate = Expr(context);
        context.match(Type.RIGHT_BRACKET);
        return predicate;
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

        private Token match(short type) throws XPathExpressionException {
            final Token token = tokenAt(1);
            if (token.getType() == type) {
                tokens.remove(0);
                return token;
            }
            throw new XPathParserException(token, Type.lookup(type));
        }
    }

    private static final class Axis {
        private static final short INVALID = -1;
        private static final short CHILD = 1;
        private static final short DESCENDANT = 2;
        private static final short PARENT = 3;
        private static final short ANCESTOR = 4;
        private static final short ATTRIBUTE = 9;
        private static final short SELF = 11;
        private static final short DESCENDANT_OR_SELF = 12;
        private static final short ANCESTOR_OR_SELF = 13;

        private static final Map<String, Short> LOOKUP_MAP;

        static {
            Map<String, Short> lookupMap = new HashMap<String, Short>();
            lookupMap.put("child", CHILD);
            lookupMap.put("descendant", DESCENDANT);
            lookupMap.put("parent", PARENT);
            lookupMap.put("ancestor", ANCESTOR);
            lookupMap.put("attribute", ATTRIBUTE);
            lookupMap.put("self", SELF);
            lookupMap.put("descendant-or-self", DESCENDANT_OR_SELF);
            lookupMap.put("ancestor-or-self", ANCESTOR_OR_SELF);
            LOOKUP_MAP = Collections.unmodifiableMap(lookupMap);
        }

        private static short lookup(Token axisToken) {
            Short axis = LOOKUP_MAP.get(axisToken.getToken());
            return null == axis ? INVALID : axis;
        }

        private Axis() { }
    }

}
