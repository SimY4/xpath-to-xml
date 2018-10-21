package com.github.simy4.xpath
package scala

import java.util.stream.Stream

import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import javax.xml.namespace.NamespaceContext
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{ Arguments, ArgumentsProvider, ArgumentsSource }

import _root_.scala.xml.{ Elem, NamespaceBinding, Node, Null, PrettyPrinter, TopScope, XML }

class DataProvider extends ArgumentsProvider {
  import Arguments._

  private val namespaceContext = new SimpleNamespaceContext
  private val namespaceBinding = NamespaceBinding("my", namespaceContext.getNamespaceURI("my"), TopScope)

  override def provideArguments(context: ExtensionContext): Stream[_ <: Arguments] = Stream.of(
    arguments(new FixtureAccessor("simple"), null, <breakfast_menu/>),
    arguments(new FixtureAccessor("simple"), namespaceContext, <breakfast_menu/>),
    arguments(new FixtureAccessor("ns-simple"), namespaceContext, Elem("my", "breakfast_menu", Null,
      namespaceBinding, minimizeEmpty = true))
  )
}

class XmlBuilderTest {
  import Assertions._

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPaths(fixtureAccessor: FixtureAccessor, namespaceContext: NamespaceContext,
                                         root: Node): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    val builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet).build(root)
    assertThat(xmlToString(builtDocument)) isEqualTo fixtureAccessor.getPutXml
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPathsAndSetValues(fixtureAccessor: FixtureAccessor,
                                                     namespaceContext: NamespaceContext, root: Node): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    val builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(root)
    assertThat(xmlToString(builtDocument)) isEqualTo fixtureAccessor.getPutValueXml
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldModifyDocumentWhenXPathsAreNotTraversable(fixtureAccessor: FixtureAccessor,
                                                      namespaceContext: NamespaceContext, root: Node): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    val xml = fixtureAccessor.getPutXml
    val oldDocument = XML.loadString(xml)
    val builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument)
    assertThat(xmlToString(builtDocument)) isEqualTo fixtureAccessor.getPutValueXml
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldNotModifyDocumentWhenAllXPathsTraversable(fixtureAccessor: FixtureAccessor,
                                                      namespaceContext: NamespaceContext, root: Node): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    val xml = fixtureAccessor.getPutValueXml
    val oldDocument = XML.loadString(xml)
    var builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(oldDocument)
    assertThat(xmlToString(builtDocument)).isEqualTo(xml)
    builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet).build(oldDocument)
    assertThat(xmlToString(builtDocument)).isEqualTo(xml)
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldRemovePathsFromExistingXml(fixtureAccessor: FixtureAccessor, namespaceContext: NamespaceContext,
                                       root: Node): Unit = {
    val xmlProperties = fixtureAccessor.getXmlProperties
    val xml = fixtureAccessor.getPutValueXml
    val oldDocument = XML.loadString(xml)
    val builtDocument = new XmlBuilder(namespaceContext).removeAll(xmlProperties.keySet).build(oldDocument)
    assertThat(xmlToString(builtDocument)).isNotEqualTo(fixtureAccessor.getPutValueXml)
  }

  private def xmlToString(xml: Node) = {
    val lineSeparator = System.getProperty("line.separator")
    val printer = new PrettyPrinter(80, 4)
    val string = printer.format(xml).replaceAll(s">\n\\s*(\\w.+?)\n\\s*</", ">$1</") + "\n"
    string.replaceAll("\n", lineSeparator)
  }
}
