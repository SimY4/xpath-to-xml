package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.helpers.SimpleNamespaceContext;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class DomXmlBuilderBenchmark {

    private static final Map<String, NamespaceContext> NAMESPACE_CONTEXT_MAP;

    static {
        var namespaceContextMap = new HashMap<String, NamespaceContext>();
        namespaceContextMap.put("null", null);
        namespaceContextMap.put("simple", new SimpleNamespaceContext());
        NAMESPACE_CONTEXT_MAP = Collections.unmodifiableMap(namespaceContextMap);
    }

    @Param({ "simple", "ns-simple", "attr", "special" })
    public String fixtureName;

    @Param({ "null" })
    public String nsContext;

    private DocumentBuilder documentBuilder;
    private FixtureAccessor fixtureAccessor;
    private NamespaceContext namespaceContext;

    @Setup
    public void setUp() throws ParserConfigurationException {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        fixtureAccessor = new FixtureAccessor(fixtureName);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();
        namespaceContext = NAMESPACE_CONTEXT_MAP.get(nsContext);
    }

    @Benchmark
    public void shouldBuildDocumentFromSetOfXPaths(Blackhole blackhole)
            throws XPathExpressionException {
        var xmlProperties = fixtureAccessor.getXmlProperties();
        var document = documentBuilder.newDocument();
        document.setXmlStandalone(true);
        blackhole.consume(new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(document));
    }

}
