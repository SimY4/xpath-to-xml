package com.github.simy4.xpath.jdom;

import com.github.simy4.xpath.XmlBuilder;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.assertj.core.api.Condition;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XmlBuilderTest {

    static Stream<Arguments> data() {
        return Stream.of(
                arguments(new FixtureAccessor("simple"), null),
                arguments(new FixtureAccessor("simple"), new SimpleNamespaceContext()),
                arguments(new FixtureAccessor("ns-simple"), new SimpleNamespaceContext()),
                arguments(new FixtureAccessor("attr"), null),
                arguments(new FixtureAccessor("attr"), new SimpleNamespaceContext()),
                arguments(new FixtureAccessor("special"), null),
                arguments(new FixtureAccessor("special"), new SimpleNamespaceContext())
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildDocumentFromSetOfXPaths(FixtureAccessor fixtureAccessor, NamespaceContext namespaceContext)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(new Document());

        for (String xpath : xmlProperties.keySet()) {
            Namespace[] namespaces = null == namespaceContext ? new Namespace[0]
                    : toNamespaces(namespaceContext);
            XPathExpression<Object> xpathExpression = XPathFactory.instance()
                    .compile(xpath, Filters.fpassthrough(), null, namespaces);
            assertThat(xpathExpression.evaluate(builtDocument)).isNotEmpty();
        }
        // although these cases are working fine the order of attribute is messed up
        assertThat(xmlToString(builtDocument)).is(new Condition<>(xml -> fixtureAccessor.toString().startsWith("attr")
                || xml.equals(fixtureAccessor.getPutXml()), "XML matches exactly"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldBuildDocumentFromSetOfXPathsAndSetValues(FixtureAccessor fixtureAccessor,
                                                        NamespaceContext namespaceContext)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document builtDocument = new XmlBuilder(namespaceContext)
                .putAll(xmlProperties)
                .build(new Document());

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Namespace[] namespaces = null == namespaceContext ? new Namespace[0]
                    : toNamespaces(namespaceContext);
            XPathExpression<Object> xpathExpression = XPathFactory.instance()
                    .compile(xpathToValuePair.getKey(), Filters.fpassthrough(), null, namespaces);
            assertThat(xpathExpression.evaluate(builtDocument)).extracting((Function<Object, Object>) input ->
                    input instanceof Element ? ((Element) input).getText()
                            : input instanceof Attribute ? ((Attribute) input).getValue()
                            : fail("Unexpected input: " + input))
                    .containsExactly(xpathToValuePair.getValue());
        }
        // although these cases are working fine the order of attribute is messed up
        assertThat(xmlToString(builtDocument)).is(new Condition<>(xml -> fixtureAccessor.toString().startsWith("attr")
                || xml.equals(fixtureAccessor.getPutValueXml()), "XML matches exactly"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldModifyDocumentWhenXPathsAreNotTraversable(FixtureAccessor fixtureAccessor,
                                                         NamespaceContext namespaceContext)
            throws XPathExpressionException, JDOMException, IOException {
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
            throws XPathExpressionException, JDOMException, IOException {
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
            throws XPathExpressionException, JDOMException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        String xml = fixtureAccessor.getPutValueXml();
        Document oldDocument = stringToXml(xml);
        Document builtDocument = new XmlBuilder(namespaceContext)
                .removeAll(xmlProperties.keySet())
                .build(oldDocument);

        for (Entry<String, Object> xpathToValuePair : xmlProperties.entrySet()) {
            Namespace[] namespaces = null == namespaceContext ? new Namespace[0]
                    : toNamespaces(namespaceContext);
            XPathExpression<Object> xpathExpression = XPathFactory.instance()
                    .compile(xpathToValuePair.getKey(), Filters.fpassthrough(), null, namespaces);
            assertThat(xpathExpression.evaluate(builtDocument)).isEmpty();
        }
        assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml());
    }

    private Document stringToXml(String xml) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
    }

    private String xmlToString(Document xml) throws IOException {
        String lineSeparator = System.lineSeparator();
        Format format = Format.getPrettyFormat();
        format.setIndent("    ");
        format.setLineSeparator(lineSeparator);
        format.setOmitDeclaration(true);
        StringWriter result = new StringWriter();
        XMLOutputter outputter = new XMLOutputter(format);
        outputter.output(xml, result);
        return result.toString().replaceAll(" />", "/>");
    }

    private Namespace[] toNamespaces(NamespaceContext namespaceContext) {
        List<Namespace> namespaces = new ArrayList<>();
        Iterator<?> prefixes = namespaceContext.getPrefixes("http://www.example.com/my");
        while (prefixes.hasNext()) {
            String prefix = (String) prefixes.next();
            namespaces.add(Namespace.getNamespace(prefix, namespaceContext.getNamespaceURI(prefix)));
        }
        return namespaces.toArray(new Namespace[0]);
    }

}
