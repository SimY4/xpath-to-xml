package com.github.simy4.xpath.json;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    private static Stream<Arguments> data() {
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
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        JsonObject builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(jsonProvider.createObjectBuilder().build());

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildJsonFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        JsonObject builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(jsonProvider.createObjectBuilder().build());

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldModifyJsonWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutXml();
        JsonValue oldDocument = stringToJson(json);
        JsonValue builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotModifyJsonWhenAllXPathsTraversable(FixtureAccessor fixtureAccessor)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutValueXml();
        JsonValue oldDocument = stringToJson(json);
        JsonValue builtDocument = new XmlBuilder()
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
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutValueXml();
        JsonValue oldDocument = stringToJson(json);
        JsonValue builtDocument = new XmlBuilder()
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private JsonValue stringToJson(String xml) {
        return jsonProvider.createReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).readValue();
    }

    private String jsonToString(JsonValue json) {
        StringWriter sw = new StringWriter();
        jsonProvider.createWriterFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true))
                .createWriter(sw)
                .write(json);
        return sw.toString().replaceAll(" {4}", "  ");
    }

}
