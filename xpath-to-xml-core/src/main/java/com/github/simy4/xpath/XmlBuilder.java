package com.github.simy4.xpath;

import com.github.simy4.xpath.effects.PutEffect;
import com.github.simy4.xpath.effects.PutValueEffect;
import com.github.simy4.xpath.effects.RemoveEffect;
import com.github.simy4.xpath.parser.XPathParser;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * XML model modifier that works via XPath expressions processing.
 *
 * @author Alex Simkin
 * @since 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class XmlBuilder {

    private static final Iterable<NavigatorSpi> navigatorSpis = ServiceLoader.load(NavigatorSpi.class);

    private final XPathParser parser;
    private final List<Effect> effects;

    public XmlBuilder() {
        this(null);
    }

    public XmlBuilder(NamespaceContext namespaceContext) {
        this(new XPathParser(namespaceContext), Collections.emptyList());
    }

    private XmlBuilder(XPathParser parser, List<Effect> effects) {
        this.parser = parser;
        this.effects = effects;
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
     * Greedily creates all missing nodes that could be evaluated by given XPath expression and then sets given
     * value as text content to all evaluated nodes.
     *
     * @param xpath XPath to process
     * @param value value to set
     * @return {@link XmlBuilder} instance
     * @throws XPathExpressionException if xpath cannot be parsed
     * @see #putAll(Map)
     */
    public XmlBuilder put(String xpath, Object value) throws XPathExpressionException {
        return putAll(Collections.singletonMap(xpath, value));
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
        final List<Effect> effects = new ArrayList<>(this.effects);
        for (var xpath : xpaths) {
            final var expr = parser.parse(xpath);
            effects.add(new PutEffect(expr));
        }
        return new XmlBuilder(parser, effects);
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
        final List<Effect> effects = new ArrayList<>(this.effects);
        for (var xpathToValuePair : xpathToValueMap.entrySet()) {
            final var expr = parser.parse(xpathToValuePair.getKey());
            effects.add(new PutValueEffect(expr, xpathToValuePair.getValue()));
        }
        return new XmlBuilder(parser, effects);
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
        final List<Effect> effects = new ArrayList<>(this.effects);
        for (var xpath : xpaths) {
            final var expr = parser.parse(xpath);
            effects.add(new RemoveEffect(expr));
        }
        return new XmlBuilder(parser, effects);
    }

    /**
     * Evaluates collected XPath effects on a given XML model object.
     *
     * @param xml XML to process
     * @param <T> XML model type
     * @return processing result
     * @throws XmlBuilderException if XML model modification failed
     */
    public <T> T build(T xml) throws XmlBuilderException {
        for (var navigatorSpi : navigatorSpis) {
            if (navigatorSpi.canHandle(xml)) {
                return navigatorSpi.process(xml, effects);
            }
        }
        throw new XmlBuilderException("Unsupported xml model");
    }

}
