package com.github.simy4.xpath.gson;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.xpath.XPathExpressionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static Stream<Arguments> data() {
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
                .build(new JsonObject());

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor) throws XPathExpressionException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(new JsonObject());

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

    private JsonElement stringToJson(String xml) {
        return gson.fromJson(xml, JsonElement.class);
    }

    private String jsonToString(JsonElement json) {
        return gson.toJson(json).replaceAll("\n", System.getProperty("line.separator"));
    }

}
