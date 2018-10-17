package com.github.simy4.xpath;

import com.github.simy4.xpath.fixtures.FixtureAccessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import javax.json.spi.JsonProvider;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Map;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class JavaxJsonBuilderBenchmark {

    private static final JsonProvider jsonProvider = JsonProvider.provider();

    @Param({ "attr", "simple", "special" })
    public String fixtureName;

    private FixtureAccessor fixtureAccessor;

    @Setup
    public void setUp() {
        fixtureAccessor = new FixtureAccessor(fixtureName, "json");
    }

    @Benchmark
    public void shouldBuildDocumentFromSetOfXPaths(Blackhole blackhole) throws XPathExpressionException, IOException {
        Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
        blackhole.consume(new XmlBuilder()
                .putAll(xmlProperties.keySet())
                .build(jsonProvider.createObjectBuilder().build()));
    }

}
