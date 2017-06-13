package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.utils.SimpleNamespaceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class XmlBuilderTest {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static final XPathFactory xpathFactory = XPathFactory.newInstance();

    @Parameters(name = "With test fixture: {0} and namespace: {1} and XML factory: {2}")
    public static Collection<Object[]> data() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilderFactory nsAwareDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
        nsAwareDocumentBuilderFactory.setNamespaceAware(true);
        return asList(new Object[][] {
                { "simple", null, documentBuilderFactory},
                { "simple", new SimpleNamespaceContext(), documentBuilderFactory},
                { "ns-simple", new SimpleNamespaceContext(), nsAwareDocumentBuilderFactory},
        });
    }

    @Parameter(0)
    public String fixtureName;
    @Parameter(1)
    public NamespaceContext namespaceContext;
    @Parameter(2)
    public DocumentBuilderFactory documentBuilderFactory;

    private DocumentBuilder documentBuilder;
    private FixtureAccessor fixtureAccessor;

    @Before
    public void setUp() throws ParserConfigurationException {
        fixtureAccessor = new FixtureAccessor(fixtureName);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPaths()
            throws XPathExpressionException, TransformerException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(documentBuilder.newDocument());

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            XPathExpression xpath = xpathFactory.newXPath().compile(xpathToValuePair.getKey());
            assertThat(xpath.evaluate(builtDocument, XPathConstants.STRING)).isNotNull();
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPathsAndSetValues()
            throws XPathExpressionException, TransformerException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(documentBuilder.newDocument());

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            XPath xpath = xpathFactory.newXPath();
            if (null != namespaceContext) {
                xpath.setNamespaceContext(namespaceContext);
            }
            assertThat(xpath.evaluate(xpathToValuePair.getKey(), builtDocument, XPathConstants.STRING)).isNotNull();
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyDocumentWhenXPathsAreNotTraversable()
            throws XPathExpressionException, TransformerException, IOException, SAXException {
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
            throws XPathExpressionException, TransformerException, IOException, SAXException {
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

    private Document stringToXml(String xml) throws IOException, SAXException {
        return documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
    }

    private String xmlToString(Document xml) throws TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(xml), new StreamResult(out));
        return out.toString();
    }

}
