package com.github.simy4.xpath.xom;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.XPathContext;
import org.assertj.core.api.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

    private static Stream<Arguments> data() {
        Element nsAwareRoot = new Element("breakfast_menu", "http://www.example.com/my");
        nsAwareRoot.setNamespacePrefix("my");
        return Stream.of(
                arguments(new FixtureAccessor("simple"), null, new Element("breakfast_menu")),
                arguments(new FixtureAccessor("simple"), new SimpleNamespaceContext(),
                        new Element("breakfast_menu")),
                arguments(new FixtureAccessor("ns-simple"), new SimpleNamespaceContext(), nsAwareRoot),
                arguments(new FixtureAccessor("attr"), null, new Element("breakfast_menu")),
                arguments(new FixtureAccessor("attr"), new SimpleNamespaceContext(),
                        new Element("breakfast_menu")),
                arguments(new FixtureAccessor("special"), null, new Element("records")),
                arguments(new FixtureAccessor("special"), new SimpleNamespaceContext(), new Element("records"))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildDocumentFromSetOfXPaths(FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext,
                                            Element root) throws XPathExpressionException, IOException {
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
        // although these cases are working fine the order of attribute is messed up
        assertThat(xmlToString(builtDocument)).is(new Condition<>(xml -> fixtureAccessor.toString().startsWith("attr")
                || xml.equals(fixtureAccessor.getPutXml()), "XML matches exactly"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildDocumentFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor,
                                                        NamespaceContext namespaceContext,
                                                        Element root) throws XPathExpressionException, IOException {
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
        // although these cases are working fine the order of attribute is messed up
        assertThat(xmlToString(builtDocument)).is(new Condition<>(xml -> fixtureAccessor.toString().startsWith("attr")
                || xml.equals(fixtureAccessor.getPutValueXml()), "XML matches exactly"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldModifyDocumentWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor,
                                                         NamespaceContext namespaceContext)
            throws XPathExpressionException, ParsingException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(oldDocument);

        assertThat(xmlToString(builtDocument)).isEqualTo(fixtureAccessor.getPutValueXml());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotModifyDocumentWhenAllXPathsTraversable(FixtureAccessor fixtureAccessor,
                                                         NamespaceContext namespaceContext)
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

    @ParameterizedTest
    @MethodSource("data")
    void shouldRemovePathsFromExistingXml(FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
            throws XPathExpressionException, ParsingException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutValueXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Nodes nodes = null == namespaceContext
                    ? builtDocument.query(xpathToValuePair.getKey())
                    : builtDocument.query(xpathToValuePair.getKey(), toXpathContext(namespaceContext));
            assertThat(nodes.size()).isEqualTo(0);
        }
        assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private Document stringToXml(String xml) throws ParsingException, IOException {
        Builder builder = new Builder();
        return builder.build(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
    }

    private String xmlToString(Document xml) throws IOException {
        String lineSeparator = System.getProperty("line.separator");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(outputStream, "UTF-8");
        serializer.setIndent(4);
        serializer.setLineSeparator(lineSeparator);
        serializer.write(xml);
        String xmlString = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        return xmlString.substring(xmlString.indexOf(lineSeparator) + lineSeparator.length());
    }

    private XPathContext toXpathContext(NamespaceContext namespaceContext) {
        XPathContext context = new XPathContext();
        Iterator<?> prefixes = namespaceContext.getPrefixes("http://www.example.com/my");
        while (prefixes.hasNext()) {
            String prefix = (String) prefixes.next();
            context.addNamespace(prefix, namespaceContext.getNamespaceURI(prefix));
        }
        return context;
    }

}
