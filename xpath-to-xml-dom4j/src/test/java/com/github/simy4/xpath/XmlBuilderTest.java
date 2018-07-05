package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.util.SimpleNamespaceContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
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
import java.nio.charset.Charset;
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
                { new FixtureAccessor("simple"), null },
                { new FixtureAccessor("simple"), new SimpleNamespaceContext() },
                { new FixtureAccessor("ns-simple"), new SimpleNamespaceContext() },
                // TODO although these cases are working fine the order of attribute is messed up
                // { new FixtureAccessor("attr"), null },
                // { new FixtureAccessor("attr"), new SimpleNamespaceContext() },
                { new FixtureAccessor("special"), null },
                { new FixtureAccessor("special"), new SimpleNamespaceContext() },
        });
    }

    @Parameter(0)
    public FixtureAccessor fixtureAccessor;
    @Parameter(1)
    public NamespaceContext namespaceContext;

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

    @Test
    public void shouldRemovePathsFromExistingXml()
            throws XPathExpressionException, DocumentException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutValueXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            XPath xpath = builtDocument.createXPath(xpathToValuePair.getKey());
            if (null != namespaceContext) {
                xpath.setNamespaceContext(new SimpleNamespaceContextWrapper(namespaceContext));
            }
            assertThat(xpath.selectNodes(builtDocument)).isEmpty();
        }
        assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private Document stringToXml(String xml) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        return saxReader.read(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
    }

    private String xmlToString(Document xml) throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        OutputFormat format = OutputFormat.createCompactFormat();
        format.setIndentSize(4);
        format.setNewlines(true);
        format.setLineSeparator(lineSeparator);
        format.setSuppressDeclaration(true);
        StringWriter result = new StringWriter();
        XMLWriter writer = new XMLWriter(result, format);
        writer.write(xml);
        return result.toString().replaceFirst(lineSeparator, "");
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
