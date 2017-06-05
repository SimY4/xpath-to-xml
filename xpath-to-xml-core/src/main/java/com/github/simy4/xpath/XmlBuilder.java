package com.github.simy4.xpath;

import com.github.simy4.xpath.action.Action;
import com.github.simy4.xpath.action.PutAction;
import com.github.simy4.xpath.action.RemoveAction;
import com.github.simy4.xpath.action.PutValueAction;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.navigator.NavigatorSpi;
import com.github.simy4.xpath.parser.XPathParser;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

/**
 * XML model modifier that works via XPath expressions processing.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@Immutable
public class XmlBuilder {

    private static final Iterable<NavigatorSpi> NAVIGATOR_SPIS = ServiceLoader.load(NavigatorSpi.class);

    private final XPathParser parser;
    private final Collection<Action> actions;

    public XmlBuilder() {
        this(null);
    }

    public XmlBuilder(@Nullable NamespaceContext namespaceContext) {
        this(new XPathParser(namespaceContext), Collections.<Action>emptyList());
    }

    private XmlBuilder(XPathParser parser, Collection<Action> actions) {
        this.parser = parser;
        this.actions = actions;
    }

    /**
     * Greedily creates all missing nodes that could be evaluated by given XPath expression.
     *
     * @param xpath XPath to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #putAll(Iterable)
     */
    public XmlBuilder put(String xpath) throws XPathExpressionException {
        return putAll(Collections.singletonList(xpath));
    }

    /**
     * Greedily creates all missing nodes that could be evaluated by given XPath expressions.
     *
     * @param xpaths XPaths to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #putAll(Iterable)
     */
    public XmlBuilder putAll(String... xpaths) throws XPathExpressionException {
        return putAll(Arrays.asList(xpaths));
    }

    /**
     * Greedily creates all missing nodes that could be evaluated by given XPath expressions.
     *
     * @param xpaths XPaths to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #put(String)
     * @see #putAll(String...)
     */
    public XmlBuilder putAll(Iterable<String> xpaths) throws XPathExpressionException {
        final Collection<Action> exprs = new ArrayList<Action>(this.actions);
        for (String xpath : xpaths) {
            final Expr expr = parser.parse(xpath);
            exprs.add(new PutAction(expr));
        }
        return new XmlBuilder(parser, exprs);
    }

    /**
     * Greedily creates all missing nodes that could be evaluated by given XPath expression and then sets given
     * value as text content to all evaluated nodes.
     *
     * @param xpath XPath to process
     * @param value value to set
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #putAll(Map)
     */
    public XmlBuilder put(String xpath, @Nullable Object value) throws XPathExpressionException {
        return putAll(Collections.singletonMap(xpath, value));
    }

    /**
     * Greedily creates all missing nodes that could be evaluated by given XPath expressions and then sets the
     * associated value as text content to all evaluated nodes.
     *
     * @param xpathToValueMap XPath to values associations
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #put(String, Object)
     */
    public XmlBuilder putAll(Map<String, Object> xpathToValueMap) throws XPathExpressionException {
        final Collection<Action> exprs = new ArrayList<Action>(this.actions);
        for (Entry<String, Object> xpathToValuePair : xpathToValueMap.entrySet()) {
            final Expr expr = parser.parse(xpathToValuePair.getKey());
            exprs.add(new PutValueAction(expr, xpathToValuePair.getValue()));
        }
        return new XmlBuilder(parser, exprs);
    }

    /**
     * Evaluates given XPath expression and detaches all of the resulting nodes.
     *
     * @param xpath XPath to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #removeAll(Iterable)
     */
    public XmlBuilder remove(String xpath) throws XPathExpressionException {
        return removeAll(Collections.singletonList(xpath));
    }

    /**
     * Evaluates given XPath expressions and detaches all of the resulting nodes.
     *
     * @param xpaths XPaths to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #removeAll(Iterable)
     */
    public XmlBuilder removeAll(String... xpaths) throws XPathExpressionException {
        return removeAll(Arrays.asList(xpaths));
    }

    /**
     * Evaluates given XPath expressions and detaches all of the resulting nodes.
     *
     * @param xpaths XPaths to process
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #remove(String)
     * @see #removeAll(String...)
     */
    public XmlBuilder removeAll(Iterable<String> xpaths) throws XPathExpressionException {
        final Collection<Action> exprs = new ArrayList<Action>(this.actions);
        for (String xpath : xpaths) {
            final Expr expr = parser.parse(xpath);
            exprs.add(new RemoveAction(expr));
        }
        return new XmlBuilder(parser, exprs);
    }

    /**
     * Evaluates collected XPath actions on a given XML model object.
     *
     * @param xml XML to process
     * @param <T> XML model type
     * @return processing result
     * @throws XmlBuilderException if XML model modification failed
     */
    public <T> T build(T xml) throws XmlBuilderException {
        for (NavigatorSpi navigatorSpi : NAVIGATOR_SPIS) {
            if (navigatorSpi.canHandle(xml)) {
                return navigatorSpi.process(xml, actions);
            }
        }
        throw new XmlBuilderException("Unsupported xml model");
    }

}
