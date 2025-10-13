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
package com.github.simy4.xpath.gson;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.assertj.core.presentation.StandardRepresentation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet()).build(new JsonObject());

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
        new XmlBuilder(namespaceContext).putAll(xmlProperties).build(new JsonObject());

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

  private JsonElement stringToJson(String xml) {
    return gson.fromJson(xml, JsonElement.class);
  }

  final class JSONRepresentation extends StandardRepresentation {
    @Override
    protected String fallbackToStringOf(Object object) {
      return object instanceof JsonElement
          ? gson.toJson((JsonElement) object)
          : super.fallbackToStringOf(object);
    }
  }
}
