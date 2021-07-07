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
package com.github.simy4.xpath;

import com.github.simy4.xpath.effects.PutEffect;
import com.github.simy4.xpath.effects.PutValueEffect;
import com.github.simy4.xpath.effects.RemoveEffect;
import com.github.simy4.xpath.expr.Expr;
import com.github.simy4.xpath.parser.XPathParser;
import com.github.simy4.xpath.spi.Effect;
import com.github.simy4.xpath.spi.NavigatorSpi;
import com.github.simy4.xpath.util.Function;
import com.github.simy4.xpath.util.LazyMemoizedServiceLoader;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * XML model modifier that works via XPath expressions processing.
 *
 * @author Alex Simkin
 * @since 1.0
 */
public class XmlBuilder implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Function<? super Class<NavigatorSpi>, ? extends Iterable<NavigatorSpi>>
      serviceLoader = new LazyMemoizedServiceLoader<NavigatorSpi>();

  /**
   * Checks whether {@link XmlBuilder} supports given XML model.
   *
   * @param xml XML to check
   * @return {@code true} if it can handle given model or {@code false} otherwise
   */
  public static boolean canHandle(Object xml) {
    for (NavigatorSpi navigatorSpi : serviceLoader.apply(NavigatorSpi.class)) {
      if (navigatorSpi.canHandle(xml)) {
        return true;
      }
    }
    return false;
  }

  private final XPathParser parser;
  private final List<Effect> effects;

  public XmlBuilder() {
    this(null);
  }

  public XmlBuilder(NamespaceContext namespaceContext) {
    this(new XPathParser(namespaceContext), Collections.<Effect>emptyList());
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
   * Greedily creates all missing nodes that could be evaluated by given XPath expression and then
   * sets given value as text content to all evaluated nodes.
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
    final List<Effect> effects = new ArrayList<Effect>(this.effects.size() + 1);
    effects.addAll(this.effects);
    for (String xpath : xpaths) {
      final Expr expr = parser.parse(xpath);
      effects.add(new PutEffect(expr));
    }
    return new XmlBuilder(parser, effects);
  }

  /**
   * Greedily creates all missing nodes that could be evaluated by given XPath expressions and then
   * sets the associated value as text content to all evaluated nodes.
   *
   * @param xpathToValueMap XPath to values associations
   * @return {@link XmlBuilder} instance
   * @throws XPathExpressionException if xpath cannot be parsed
   * @see #put(String, Object)
   */
  public XmlBuilder putAll(Map<String, Object> xpathToValueMap) throws XPathExpressionException {
    final List<Effect> effects =
        new ArrayList<Effect>(this.effects.size() + xpathToValueMap.size());
    effects.addAll(this.effects);
    for (Map.Entry<String, Object> xpathToValuePair : xpathToValueMap.entrySet()) {
      final Expr expr = parser.parse(xpathToValuePair.getKey());
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
    final List<Effect> effects = new ArrayList<Effect>(this.effects.size() + 1);
    effects.addAll(this.effects);
    for (String xpath : xpaths) {
      final Expr expr = parser.parse(xpath);
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
    for (NavigatorSpi navigatorSpi : serviceLoader.apply(NavigatorSpi.class)) {
      if (navigatorSpi.canHandle(xml)) {
        return navigatorSpi.process(xml, effects);
      }
    }
    throw new XmlBuilderException("Unsupported xml model");
  }
}
