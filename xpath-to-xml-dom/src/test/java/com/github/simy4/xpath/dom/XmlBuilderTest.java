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
package com.github.simy4.xpath.dom;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

  private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
  private static final XPathFactory xpathFactory = XPathFactory.newInstance();

  static Stream<Arguments> data() {
    var documentBuilderFactory = DocumentBuilderFactory.newInstance();
    var nsAwareDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
    nsAwareDocumentBuilderFactory.setNamespaceAware(true);
    return Stream.of(
        arguments(new FixtureAccessor("simple"), null, documentBuilderFactory),
        arguments(
            new FixtureAccessor("simple"), new SimpleNamespaceContext(), documentBuilderFactory),
        arguments(
            new FixtureAccessor("ns-simple"),
            new SimpleNamespaceContext(),
            nsAwareDocumentBuilderFactory),
        arguments(new FixtureAccessor("attr"), null, documentBuilderFactory),
        arguments(
            new FixtureAccessor("attr"), new SimpleNamespaceContext(), documentBuilderFactory),
        arguments(new FixtureAccessor("special"), null, documentBuilderFactory),
        arguments(
            new FixtureAccessor("special"), new SimpleNamespaceContext(), documentBuilderFactory));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildDocumentFromSetOfXPaths(
      FixtureAccessor fixtureAccessor,
      NamespaceContext namespaceContext,
      DocumentBuilderFactory documentBuilderFactory)
      throws XPathExpressionException, TransformerException, ParserConfigurationException {
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var document = documentBuilder.newDocument();
    document.setXmlStandalone(true);
    var builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(document);

    for (var xpathString : xmlProperties.keySet()) {
      var xpath = xpathFactory.newXPath();
      if (null != namespaceContext) {
        xpath.setNamespaceContext(namespaceContext);
      }
      assertThat(xpath.evaluate(xpathString, builtDocument)).isNotNull();
    }
    assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildDocumentFromSetOfXPathsAndSetValues(
      FixtureAccessor fixtureAccessor,
      NamespaceContext namespaceContext,
      DocumentBuilderFactory documentBuilderFactory)
      throws XPathExpressionException, TransformerException, ParserConfigurationException {
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var document = documentBuilder.newDocument();
    document.setXmlStandalone(true);
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(document);

    for (var xpathToValuePair : xmlProperties.entrySet()) {
      var xpath = xpathFactory.newXPath();
      if (null != namespaceContext) {
        xpath.setNamespaceContext(namespaceContext);
      }
      assertThat(xpath.evaluate(xpathToValuePair.getKey(), builtDocument, XPathConstants.STRING))
          .isEqualTo(xpathToValuePair.getValue());
    }
    assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldModifyDocumentWhenXPathsAreNotTraversable(
      FixtureAccessor fixtureAccessor,
      NamespaceContext namespaceContext,
      DocumentBuilderFactory documentBuilderFactory)
      throws XPathExpressionException,
          TransformerException,
          IOException,
          SAXException,
          ParserConfigurationException {
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var xml = fixtureAccessor.getPutXml();
    var oldDocument = stringToXml(documentBuilder, xml);
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldNotModifyDocumentWhenAllXPathsTraversable(
      FixtureAccessor fixtureAccessor,
      NamespaceContext namespaceContext,
      DocumentBuilderFactory documentBuilderFactory)
      throws XPathExpressionException,
          TransformerException,
          IOException,
          SAXException,
          ParserConfigurationException {
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var xml = fixtureAccessor.getPutValueXml();
    var oldDocument = stringToXml(documentBuilder, xml);
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(xml);

    builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(xmlToString(builtDocument)).isEqualTo(xml);
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldRemovePathsFromExistingXml(
      FixtureAccessor fixtureAccessor,
      NamespaceContext namespaceContext,
      DocumentBuilderFactory documentBuilderFactory)
      throws XPathExpressionException,
          TransformerException,
          IOException,
          SAXException,
          ParserConfigurationException {
    var documentBuilder = documentBuilderFactory.newDocumentBuilder();
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var xml = fixtureAccessor.getPutValueXml();
    var oldDocument = stringToXml(documentBuilder, xml);
    var builtDocument =
        new XmlBuilder(namespaceContext).removeAll(xmlProperties.keySet()).build(oldDocument);

    for (var xpathToValuePair : xmlProperties.entrySet()) {
      var xpath = xpathFactory.newXPath();
      if (null != namespaceContext) {
        xpath.setNamespaceContext(namespaceContext);
      }
      assertThat(xpath.evaluate(xpathToValuePair.getKey(), builtDocument, XPathConstants.NODE))
          .isNull();
    }
    assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
  }

  private Document stringToXml(DocumentBuilder documentBuilder, String xml)
      throws IOException, SAXException {
    var document =
        documentBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    document.setXmlStandalone(true);
    return document;
  }

  private String xmlToString(Document xml) throws TransformerException {
    var transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    var out = new StringWriter();
    transformer.transform(new DOMSource(xml), new StreamResult(out));
    return out.toString().replaceAll("\n\\p{Space}*\n", "\n"); // JDK 9 fix
  }
}
