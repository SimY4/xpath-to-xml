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
