/*
 * Copyright 2018-2025 Alex Simkin
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
import org.assertj.core.presentation.StandardRepresentation;
import org.json.JSONObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

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
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(new JSONObject());

    assertThat(builtDocument)
        .usingEquals(JSONObject::similar)
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
        new XmlBuilder(namespaceContext).putAll(xmlProperties).build(new JSONObject());

    assertThat(builtDocument)
        .usingEquals(JSONObject::similar)
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
        .usingEquals(JSONObject::similar)
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
        .usingEquals(JSONObject::similar)
        .withRepresentation(new JSONRepresentation())
        .isEqualTo(stringToJson(json));

    builtDocument =
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(builtDocument)
        .usingEquals(JSONObject::similar)
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
        .usingEquals(JSONObject::similar)
        .withRepresentation(new JSONRepresentation())
        .isNotEqualTo(stringToJson(fixtureAccessor.getPutValueXml()));
  }

  private JSONObject stringToJson(String xml) {
    return new JSONObject(xml);
  }

  static final class JSONRepresentation extends StandardRepresentation {
    @Override
    protected String fallbackToStringOf(Object object) {
      return object instanceof JSONObject
          ? ((JSONObject) object).toString(2)
          : super.fallbackToStringOf(object);
    }
  }
}
