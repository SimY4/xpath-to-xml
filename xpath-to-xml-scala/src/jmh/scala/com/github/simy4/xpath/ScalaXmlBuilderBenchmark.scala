/*
 * Copyright 2021 Alex Simkin
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
package com.github.simy4.xpath

import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import javax.xml.namespace.NamespaceContext
import org.openjdk.jmh.annotations.{ Benchmark, BenchmarkMode, Mode, Param, Scope, Setup, State }
import org.openjdk.jmh.infra.Blackhole

import xml.Elem

@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class ScalaXmlBuilderBenchmark {

  @Param(Array("simple", "ns-simple", "attr", "special"))
  var fixtureName: String = _

  @Param(Array("null"))
  var nsContext: String = _

  private var root: Elem                         = _
  private var fixtureAccessor: FixtureAccessor   = _
  private var namespaceContext: NamespaceContext = _

  @Setup
  def setUp(): Unit = {
    root = if ("special" == fixtureName) <records/> else <breakfast_menu/>
    fixtureAccessor = new FixtureAccessor(fixtureName)
    namespaceContext = ScalaXmlBuilderBenchmark.namespaceContextMap(nsContext)
  }

  @Benchmark
  def shouldBuildDocumentFromSetOfXPaths(blackhole: Blackhole): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    blackhole.consume(
      new XmlBuilder(namespaceContext)
        .putAll(xmlProperties.keySet())
        .build(root)
    )
  }
}

object ScalaXmlBuilderBenchmark {
  private[ScalaXmlBuilderBenchmark] val namespaceContextMap: Map[String, NamespaceContext] = Map(
    "null"   -> null,
    "simple" -> new SimpleNamespaceContext
  )
}
