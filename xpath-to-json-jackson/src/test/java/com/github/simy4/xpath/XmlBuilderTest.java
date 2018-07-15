package com.github.simy4.xpath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
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

    @Parameters(name = "With test fixture: {0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                { new FixtureAccessor("attr", "json") },
                { new FixtureAccessor("simple", "json") },
                { new FixtureAccessor("special", "json") },
        });
    }

    @Parameter(0)
    public FixtureAccessor fixtureAccessor;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldBuildJsonFromSetOfXPaths() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        ObjectNode builtDocument = new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(new ObjectNode(JsonNodeFactory.instance));

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildJsonFromSetOfXPathsAndSetValues() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        ObjectNode builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(new ObjectNode(JsonNodeFactory.instance));

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyJsonWhenXPathsAreNotTraversable() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutXml();
        JsonNode oldDocument = stringToJson(json);
        JsonNode builtDocument = new XmlBuilder()
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldNotModifyJsonWhenAllXPathsTraversable() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String json = fixtureAccessor.getPutValueXml();
        JsonNode oldDocument = stringToJson(json);
        JsonNode builtDocument = new XmlBuilder()
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
        JsonNode oldDocument = stringToJson(json);
        JsonNode builtDocument = new XmlBuilder()
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(jsonToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private JsonNode stringToJson(String xml) throws IOException {
        return objectMapper.readTree(xml);
    }

    private String jsonToString(JsonNode json) throws JsonProcessingException {
        return objectMapper.writer(new DefaultPrettyPrinter() {
            {
                _objectFieldValueSeparatorWithSpaces = _separators.getObjectFieldValueSeparator() + " ";
                _arrayIndenter = new DefaultIndenter();
                _objectIndenter = new DefaultIndenter();
            }
        }).writeValueAsString(json).replaceAll("\\{ }", "{}");
    }

}
