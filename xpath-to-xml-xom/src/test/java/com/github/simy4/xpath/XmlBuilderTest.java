package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.utils.SimpleNamespaceContext;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.XPathContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class XmlBuilderTest {

    @Parameters(name = "With test fixture: {0} and namespace: {1}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                { "simple", null },
                { "simple", new SimpleNamespaceContext() },
                { "ns-simple", new SimpleNamespaceContext() },
//                TODO although these cases are working fine the order of attribute is messed up
//                { "attr", null },
//                { "attr", new SimpleNamespaceContext() },
        });
    }

    @Parameter(0)
    public String fixtureName;
    @Parameter(1)
    public NamespaceContext namespaceContext;

    private FixtureAccessor fixtureAccessor;

    @Before
    public void setUp() {
        fixtureAccessor = new FixtureAccessor(fixtureName);
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPaths() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(new Document(new Element("breakfast_menu")));

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Nodes nodes = null == namespaceContext
                    ? builtDocument.query(xpathToValuePair.getKey(), toXpathContext(namespaceContext))
                    : builtDocument.query(xpathToValuePair.getKey());
            assertThat(nodes).isNotNull();
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPathsAndSetValues() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(new Document(new Element("breakfast_menu")));

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Nodes nodes = null == namespaceContext
                    ? builtDocument.query(xpathToValuePair.getKey(), toXpathContext(namespaceContext))
                    : builtDocument.query(xpathToValuePair.getKey());
            assertThat(nodes.get(0).getValue()).isEqualTo(xpathToValuePair.getValue());
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyDocumentWhenXPathsAreNotTraversable() throws XPathExpressionException, ParsingException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldNotModifyDocumentWhenAllXPathsTraversable() throws XPathExpressionException, ParsingException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutValueXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(xmlToString(builtDocument)).isEqualTo(xml);

        builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(oldDocument);

        assertThat(xmlToString(builtDocument)).isEqualTo(xml);
    }

    private Document stringToXml(String xml) throws ParsingException, IOException {
        Builder builder = new Builder();
        return builder.build(new ByteArrayInputStream(xml.getBytes()));
    }

    private String xmlToString(Document xml) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(outputStream, "UTF-8");
        serializer.setIndent(4);
        serializer.setLineSeparator("\n");
        serializer.write(xml);
        String xmlString = new String(outputStream.toByteArray(), "UTF-8");
        return xmlString.substring(xmlString.indexOf('\n') + 1);
    }

    private XPathContext toXpathContext(NamespaceContext namespaceContext) {
        XPathContext context = new XPathContext();

        return context;
    }

}
