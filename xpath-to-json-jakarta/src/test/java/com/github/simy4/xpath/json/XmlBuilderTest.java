package com.github.simy4.xpath.json;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
                arguments(new FixtureAccessor("attr", "json")),
                arguments(new FixtureAccessor("simple", "json")),
                arguments(new FixtureAccessor("special", "json"))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPaths(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(JsonValue.EMPTY_JSON_OBJECT);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(JsonValue.EMPTY_JSON_OBJECT);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldModifyJsonWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
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
    void shouldNotModifyJsonWhenAllXPathsTraversable(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
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
    void shouldRemovePathsFromExistingXml(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var json = fixtureAccessor.getPutValueXml();
        var oldDocument = stringToJson(json);
        var builtDocument = new XmlBuilder()
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private JsonValue stringToJson(String xml) {
        return Json.createReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).readValue();
    }

    private String jsonToString(JsonValue json) {
        var lineSeparator = System.lineSeparator();
        var sw = new StringWriter();
        Json.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
                .createWriter(sw)
                .write(json);
        return sw.toString()
                .replaceAll(" {4}", "  ")
                .replaceAll("\\{\n\\p{Space}*}", "{}")
                .replaceAll("\n", lineSeparator);
    }

}
