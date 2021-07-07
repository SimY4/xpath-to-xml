/*
 * Copyright 2017-2021 Alex Simkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.simy4.xpath;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.simy4.xpath.fixtures.FixtureAccessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import javax.xml.xpath.XPathExpressionException;

import java.util.Map;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class JacksonJsonBuilderBenchmark {

  @Param({"attr", "simple", "special"})
  public String fixtureName;

  private FixtureAccessor fixtureAccessor;

  @Setup
  public void setUp() {
    fixtureAccessor = new FixtureAccessor(fixtureName, "json");
  }

  @Benchmark
  public void shouldBuildDocumentFromSetOfXPaths(Blackhole blackhole)
      throws XPathExpressionException {
    Map<String, Object> xmlProperties = fixtureAccessor.getXmlProperties();
    blackhole.consume(
        new XmlBuilder()
            .putAll(xmlProperties.keySet())
            .build(new ObjectNode(JsonNodeFactory.instance)));
  }
}
