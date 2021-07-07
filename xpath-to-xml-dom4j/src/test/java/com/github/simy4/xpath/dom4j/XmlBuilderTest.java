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
package com.github.simy4.xpath.dom4j;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.assertj.core.api.Condition;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

  static Stream<Arguments> data() {
    return Stream.of(
        arguments(new FixtureAccessor("simple"), null),
        arguments(new FixtureAccessor("simple"), new SimpleNamespaceContext()),
        arguments(new FixtureAccessor("ns-simple"), new SimpleNamespaceContext()),
        arguments(new FixtureAccessor("attr"), null),
        arguments(new FixtureAccessor("attr"), new SimpleNamespaceContext()),
        arguments(new FixtureAccessor("special"), null),
        arguments(new FixtureAccessor("special"), new SimpleNamespaceContext()));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildDocumentFromSetOfXPaths(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    Document builtDocument =
        new XmlBuilder(namespaceContext)
            .putAll(xmlProperties.keySet())
            .build(DocumentHelper.createDocument());

    for (String xpathString : xmlProperties.keySet()) {
      XPath xpath = builtDocument.createXPath(xpathString);
      if (null != namespaceContext) {
        xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
      }
      assertThat(xpath.evaluate(builtDocument)).isNotNull();
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument))
        .is(
            new Condition<>(
                xml ->
                    fixtureAccessor.toString().startsWith("attr")
                        || xml.equals(fixtureAccessor.getPutXml()),
                "XML matches exactly"));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildDocumentFromSetOfXPathsAndSetValues(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    Document builtDocument =
        new XmlBuilder(namespaceContext)
            .putAll(xmlProperties)
            .build(DocumentHelper.createDocument());

    for (Map.Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
      XPath xpath = builtDocument.createXPath(xpathToValuePair.getKey());
      if (null != namespaceContext) {
        xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
      }
      assertThat(xpath.selectSingleNode(builtDocument).getText())
          .isEqualTo(xpathToValuePair.getValue());
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument))
        .is(
            new Condition<>(
                xml ->
                    fixtureAccessor.toString().startsWith("attr")
                        || xml.equals(fixtureAccessor.getPutValueXml()),
                "XML matches exactly"));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldModifyDocumentWhenXPathsAreNotTraversable(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException, DocumentException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String xml = fixtureAccessor.getPutXml();
    Document oldDocument = stringToXml(xml);
    Document builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldNotModifyDocumentWhenAllXPathsTraversable(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException, DocumentException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String xml = fixtureAccessor.getPutValueXml();
    Document oldDocument = stringToXml(xml);
    Document builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(xml);

    builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(xml);
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldRemovePathsFromExistingXml(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException, DocumentException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String xml = fixtureAccessor.getPutValueXml();
    Document oldDocument = stringToXml(xml);
    Document builtDocument =
        new XmlBuilder(namespaceContext).removeAll(xmlProperties.keySet()).build(oldDocument);

    for (Map.Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
      XPath xpath = builtDocument.createXPath(xpathToValuePair.getKey());
      if (null != namespaceContext) {
        xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
      }
      assertThat(xpath.selectNodes(builtDocument)).isEmpty();
    }
    assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
  }

  private Document stringToXml(String xml) throws DocumentException {
    SAXReader saxReader = new SAXReader();
    return saxReader.read(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
  }

  private String xmlToString(Document xml) throws IOException {
    String lineSeparator = System.lineSeparator();
    OutputFormat format = OutputFormat.createCompactFormat();
    format.setIndentSize(4);
    format.setNewlines(true);
    format.setLineSeparator(lineSeparator);
    format.setSuppressDeclaration(true);
    StringWriter result = new StringWriter();
    XMLWriter writer = new XMLWriter(result, format);
    writer.write(xml);
    return result.toString().replaceFirst(lineSeparator, "");
  }

  private static final class SimpleNamespaceContextWrapper implements org.jaxen.NamespaceContext {

    private final NamespaceContext namespaceContext;

    private SimpleNamespaceContextWrapper(NamespaceContext namespaceContext) {
      this.namespaceContext = namespaceContext;
    }

    @Override
    public String translateNamespacePrefixToUri(String prefix) {
      return namespaceContext.getNamespaceURI(prefix);
    }
  }
}
