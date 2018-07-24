package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class XmlBuilderTest {

    @Parameter(0)
    public FixtureAccessor fixtureAccessor;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Parameters(name = "With test fixture: {0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                { new FixtureAccessor("attr", "json") },
                { new FixtureAccessor("simple", "json") },
                { new FixtureAccessor("special", "json") },
        });
    }

    @Test
    public void shouldBuildJsonFromSetOfXPaths() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        JsonObject builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(new JsonObject());

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildJsonFromSetOfXPathsAndSetValues() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        JsonObject builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(new JsonObject());

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyJsonWhenXPathsAreNotTraversable() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutXml();
        JsonElement oldDocument = stringToJson(json);
        JsonElement builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldNotModifyJsonWhenAllXPathsTraversable() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutValueXml();
        JsonElement oldDocument = stringToJson(json);
        JsonElement builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(json);

        builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(json);
    }

    @Test
    public void shouldRemovePathsFromExistingXml() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutValueXml();
        JsonElement oldDocument = stringToJson(json);
        JsonElement builtDocument = new XmlBuilder()
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
