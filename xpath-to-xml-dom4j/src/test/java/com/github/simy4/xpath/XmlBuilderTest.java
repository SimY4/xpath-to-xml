package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.utils.SimpleNamespaceContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
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

    @Parameters(name = "With test fixture: {0} and namespace: {1} and XML factory: {2}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
                { "simple", null },
                { "simple", new SimpleNamespaceContext() },
                { "ns-simple", new SimpleNamespaceContext() },
                { "attr", null },
                { "attr", new SimpleNamespaceContext() },
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
                .build(DocumentHelper.createDocument());

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            XPath xpath = builtDocument.createXPath(xpathToValuePair.getKey());
            if (null != namespaceContext) {
                xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
            }
            assertThat(xpath.evaluate(builtDocument)).isNotNull();
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutXml());
    }

    @Test
    public void shouldBuildDocumentFromSetOfXPathsAndSetValues() throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(DocumentHelper.createDocument());

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            XPath xpath = builtDocument.createXPath(xpathToValuePair.getKey());
            if (null != namespaceContext) {
                xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
            }
            assertThat(xpath.selectSingleNode(builtDocument).getText()).isEqualTo(xpathToValuePair.getValue());
        }
        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @Test
    public void shouldModifyDocumentWhenXPathsAreNotTraversable()
            throws XPathExpressionException, DocumentException, IOException {
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
            throws XPathExpressionException, DocumentException, IOException {
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

    private Document stringToXml(String xml) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        return saxReader.read(new ByteArrayInputStream(xml.getBytes()));
    }

    private String xmlToString(Document xml) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(4);
        StringWriter result = new StringWriter();
        XMLWriter writer = new XMLWriter(result, format);
        writer.write(xml.getRootElement());
        return result.toString();
    }

    private static final class SimpleNamespaceContextWrapper implements org.jaxen.NamespaceContext {

        private final NamespaceContext namespaceContext;

        private SimpleNamespaceContextWrapper(NamespaceContext namespaceContext) {
            this.namespaceContext = namespaceContext;
        }

        @Override
        public String translateNamespacePrefixToUri(String prefix) {
            return namespaceContext.getNamespaceURI(prefix);
        }

    }

}
