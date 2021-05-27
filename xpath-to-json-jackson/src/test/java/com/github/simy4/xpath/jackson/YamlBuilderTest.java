package com.github.simy4.xpath.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class YamlBuilderTest {

  private final ObjectMapper objectMapper =
      new ObjectMapper(
          YAMLFactory.builder()
              .disable(
                  YAMLGenerator.Feature.WRITE_DOC_START_MARKER, YAMLGenerator.Feature.SPLIT_LINES)
              .build());

  static Stream<Arguments> data() {
    return Stream.of(
        arguments(new FixtureAccessor("attr", "yaml")),
        arguments(new FixtureAccessor("simple", "yaml")),
        arguments(new FixtureAccessor("special", "yaml")));
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildYamlFromSetOfXPaths(FixtureAccessor fixtureAccessor)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    ObjectNode builtDocument =
        new XmlBuilder()
            .putAll(xmlProperties.keySet())
            .build(new ObjectNode(JsonNodeFactory.instance));

    assertThat(yamlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldBuildYamlFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    ObjectNode builtDocument =
        new XmlBuilder().putAll(xmlProperties).build(new ObjectNode(JsonNodeFactory.instance));

    assertThat(yamlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldModifyYamlWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String json = fixtureAccessor.getPutXml();
    JsonNode oldDocument = stringToYaml(json);
    JsonNode builtDocument = new XmlBuilder().putAll(xmlProperties).build(oldDocument);

    assertThat(yamlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldNotModifyYamlWhenAllXPathsTraversable(FixtureAccessor fixtureAccessor)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String yaml = fixtureAccessor.getPutValueXml();
    JsonNode oldDocument = stringToYaml(yaml);
    JsonNode builtDocument = new XmlBuilder().putAll(xmlProperties).build(oldDocument);

    assertThat(yamlToString(builtDocument)).isEqualTo(yaml);

    builtDocument = new XmlBuilder().putAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(yamlToString(builtDocument)).isEqualTo(yaml);
  }

  @ParameterizedTest
  @MethodSource("data")
  void shouldRemovePathsFromExistingYaml(FixtureAccessor fixtureAccessor)
      throws XPathExpressionException, IOException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    String yaml = fixtureAccessor.getPutValueXml();
    JsonNode oldDocument = stringToYaml(yaml);
    JsonNode builtDocument = new XmlBuilder().removeAll(xmlProperties.keySet()).build(oldDocument);

    assertThat(yamlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
  }

  private JsonNode stringToYaml(String xml) throws IOException {
    return objectMapper.readTree(xml);
  }

  private String yamlToString(JsonNode json) throws JsonProcessingException {
    return objectMapper.writeValueAsString(json);
  }
}
