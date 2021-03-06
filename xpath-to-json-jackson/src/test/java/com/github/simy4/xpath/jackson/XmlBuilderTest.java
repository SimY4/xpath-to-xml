package com.github.simy4.xpath.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    static Stream<Arguments> data() {
        return Stream.of(
                arguments(new FixtureAccessor("attr", "json")),
                arguments(new FixtureAccessor("simple", "json")),
                arguments(new FixtureAccessor("special", "json"))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPaths(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(new ObjectNode(JsonNodeFactory.instance));

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(new ObjectNode(JsonNodeFactory.instance));

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldModifyJsonWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var json = fixtureAccessor.getPutXml();
        var oldDocument = stringToJson(json);
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotModifyJsonWhenAllXPathsTraversable(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var json = fixtureAccessor.getPutValueXml();
        var oldDocument = stringToJson(json);
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(json);

        builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(json);
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldRemovePathsFromExistingXml(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var json = fixtureAccessor.getPutValueXml();
        var oldDocument = stringToJson(json);
        var builtDocument = new XmlBuilder()
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private JsonNode stringToJson(String xml) throws IOException {
        return objectMapper.readTree(xml);
    }

    private String jsonToString(JsonNode json) throws JsonProcessingException {
        return objectMapper.writer(new DefaultPrettyPrinter(new DefaultPrettyPrinter() {
            private static final long serialVersionUID = 1;

            {
                _objectFieldValueSeparatorWithSpaces = _separators.getObjectFieldValueSeparator() + " ";
                _arrayIndenter = new DefaultIndenter();
                _objectIndenter = new DefaultIndenter();
            }
        })).writeValueAsString(json).replaceAll("\\{ }", "{}");
    }

}
