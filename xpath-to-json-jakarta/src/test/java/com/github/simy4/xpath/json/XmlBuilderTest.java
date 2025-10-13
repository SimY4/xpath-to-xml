/*
 * Copyright 2018-2021 Alex Simkin
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
package com.github.simy4.xpath.json;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

  static Stream<Arguments> data() {
    return Stream.of(
        arguments(new FixtureAccessor("attr", "json"), null),
        arguments(new FixtureAccessor("attr", "json"), new SimpleNamespaceContext()),
        arguments(new FixtureAccessor("simple", "json"), null),
        arguments(new FixtureAccessor("simple", "json"), new SimpleNamespaceContext()),
        arguments(new FixtureAccessor("special", "json"), null),
        arguments(new FixtureAccessor("special", "json"), new SimpleNamespaceContext()));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildJsonFromSetOfXPaths(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException {
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var builtDocument =
        new XmlBuilder(namespaceContext)
            .putAll(xmlProperties.keySet())
            .build(JsonValue.EMPTY_JSON_OBJECT);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(fixtureAccessor.getPutXml()));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildJsonFromSetOfXPathsAndSetValues(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException {
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties).build(JsonValue.EMPTY_JSON_OBJECT);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(fixtureAccessor.getPutValueXml()));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldModifyJsonWhenXPathsAreNotTraversable(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException {
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var json = fixtureAccessor.getPutXml();
    var oldDocument = stringToJson(json);
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(fixtureAccessor.getPutValueXml()));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldNotModifyJsonWhenAllXPathsTraversable(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException {
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var json = fixtureAccessor.getPutValueXml();
    var oldDocument = stringToJson(json);
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(json));

    builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(json));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldRemovePathsFromExistingXml(
      FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
      throws XPathExpressionException {
    var xmlProperties = fixtureAccessor.getXmlProperties();
    var json = fixtureAccessor.getPutValueXml();
    var oldDocument = stringToJson(json);
    var builtDocument =
        new XmlBuilder(namespaceContext).removeAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(builtDocument)
        .withRepresentation(new JSONRepresentation())
        .isNotEqualTo(stringToJson(fixtureAccessor.getPutValueXml()));
  }

  private JsonValue stringToJson(String xml) {
    return Json.createReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)))
        .readValue();
  }

  static final class JSONRepresentation extends StandardRepresentation {
    @Override
    protected String fallbackToStringOf(Object object) {
      if (!(object instanceof JsonValue)) {
        return super.fallbackToStringOf(object);
      }
      var sw = new StringWriter();
      Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
          .createWriter(sw)
          .write((JsonValue) object);
      return sw.toString();
    }
  }
}
