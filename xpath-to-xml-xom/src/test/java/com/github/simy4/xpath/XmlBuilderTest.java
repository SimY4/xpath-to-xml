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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class XmlBuilderTest {

    @Parameters(name = "With test fixture: {0} and namespace: {1} and root element: {2}")
    public static Collection<Object[]> data() {
        Element nsAwareRoot = new Element("breakfast_menu", "http://www.example.com/my");
        nsAwareRoot.setNamespacePrefix("my");
        return asList(new Object[][] {
                { "simple", null, new Element("breakfast_menu") },
                { "simple", new SimpleNamespaceContext(), new Element("breakfast_menu") },
                { "ns-simple", new SimpleNamespaceContext(), nsAwareRoot },
//                TODO although these cases are working fine the order of attribute is messed up
//                { "attr", null },
//                { "attr", new SimpleNamespaceContext() },
        });
    }

    @Parameter(0)
    public String fixtureName;
    @Parameter(1)
    public NamespaceContext namespaceContext;
    @Parameter(2)
    public Element root;

    private FixtureAccessor fixtureAccessor;

    @Before
    public void setUp() {
        fixtureAccessor = new FixtureAccessor(fixtureName);
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPaths() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document newDocument = new Document((Element) root.copy());
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(newDocument);

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Nodes nodes = null == namespaceContext
                    ? builtDocument.query(xpathToValuePair.getKey())
                    : builtDocument.query(xpathToValuePair.getKey(), toXpathContext(namespaceContext));
            assertThat(nodes).isNotNull();
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPathsAndSetValues() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document newDocument = new Document((Element) root.copy());
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(newDocument);

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Nodes nodes = null == namespaceContext
                    ? builtDocument.query(xpathToValuePair.getKey())
                    : builtDocument.query(xpathToValuePair.getKey(), toXpathContext(namespaceContext));
            assertThat(nodes.get(0).getValue()).isEqualTo(xpathToValuePair.getValue());
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyDocumentWhenXPathsAreNotTraversable()
            throws XPathExpressionException, ParsingException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldNotModifyDocumentWhenAllXPathsTraversable()
            throws XPathExpressionException, ParsingException, IOException {
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
        String lineSeparator = System.getProperty("line.separator");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(outputStream, "UTF-8");
        serializer.setIndent(4);
        serializer.setLineSeparator(lineSeparator);
        serializer.write(xml);
        String xmlString = new String(outputStream.toByteArray(), "UTF-8");
        return xmlString.substring(xmlString.indexOf(lineSeparator) + lineSeparator.length());
    }

    private XPathContext toXpathContext(NamespaceContext namespaceContext) {
        XPathContext context = new XPathContext();
        Iterator prefixes = namespaceContext.getPrefixes("http://www.example.com/my");
        while (prefixes.hasNext()) {
            String prefix = (String) prefixes.next();
            context.addNamespace(prefix, namespaceContext.getNamespaceURI(prefix));
        }
        return context;
    }

}
