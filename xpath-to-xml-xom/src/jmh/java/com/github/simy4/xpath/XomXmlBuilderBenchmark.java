package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import com.github.simy4.xpath.util.SimpleNamespaceContext;
import nu.xom.Document;
import nu.xom.Element;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class XomXmlBuilderBenchmark {

    private static final Map<String, NamespaceContext> NAMESPACE_CONTEXT_MAP;

    static {
        Map<String, NamespaceContext> namespaceContextMap = new HashMap<>();
        namespaceContextMap.put("null", null);
        namespaceContextMap.put("simple", new SimpleNamespaceContext());
        NAMESPACE_CONTEXT_MAP = Collections.unmodifiableMap(namespaceContextMap);
    }

    @Param({ "simple", "ns-simple", "attr", "special" })
    public String fixtureName;

    @Param({ "null" })
    public String nsContext;

    private Element root;
    private FixtureAccessor fixtureAccessor;
    private NamespaceContext namespaceContext;

    @Setup
    public void setUp() {
        root = new Element("breakfast_menu");
        fixtureAccessor = new FixtureAccessor(fixtureName);
        namespaceContext = NAMESPACE_CONTEXT_MAP.get(nsContext);
    }

    @Benchmark
    public void shouldBuildDocumentFromSetOfXPaths(Blackhole blackhole)
            throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        Document newDocument = new Document((Element) root.copy());
        blackhole.consume(new XmlBuilder(namespaceContext)
                .putAll(xmlProperties.keySet())
                .build(newDocument));
    }

}
