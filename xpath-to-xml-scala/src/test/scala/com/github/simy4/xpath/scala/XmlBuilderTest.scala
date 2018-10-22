package com.github.simy4.xpath
package scala

import java.util.stream.Stream

import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import javax.xml.namespace.NamespaceContext
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kantan.xpath.{ CompileResult, DecodeError, ParseResult, XPathCompiler, XmlParser }
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
      namespaceBinding, minimizeEmpty = true)),
    arguments(new FixtureAccessor("special"), null, <records/>),
    arguments(new FixtureAccessor("special"), namespaceContext, <records/>)
  )
}

class XmlBuilderTest {
  import Assertions._
  import kantan.xpath.implicits._

  import _root_.scala.collection.JavaConverters._

  private def xPathCompiler(namespaceContext: NamespaceContext): XPathCompiler = Option(namespaceContext)
    .fold(XPathCompiler.builtIn) { nc =>
      val xPathFactory = XPathFactory.newInstance
      XPathCompiler { xpathString =>
        CompileResult {
          val xpath = xPathFactory.newXPath
          xpath.setNamespaceContext(nc)
          xpath.compile(xpathString)
        }
      }
    }

  private def xmlParser(namespaceContext: NamespaceContext): XmlParser = Option(namespaceContext)
    .fold(XmlParser.builtIn) { _ =>
      val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
      factory.setNamespaceAware(true)
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
      XmlParser { source => ParseResult(factory.newDocumentBuilder().parse(source)) }
    }

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
    implicit val parser: XmlParser = xmlParser(namespaceContext)
    val xmlProperties = fixtureAccessor.getXmlProperties
    val builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties).build(root)
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.asScala foreach { case (xpath, value) =>
      assertThat(for {
        xp  <- xPathCompiler(namespaceContext).compile(xpath).right
        res <- builtDocumentString.evalXPath[String](xp).right
      } yield res).as("Should evaluate XPath %s to %s", xpath, value) isEqualTo Right(value)
    }
    assertThat(builtDocumentString) isEqualTo fixtureAccessor.getPutValueXml
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
    assertThat(xmlToString(builtDocument)) isEqualTo xml
    builtDocument = new XmlBuilder(namespaceContext).putAll(xmlProperties.keySet).build(oldDocument)
    assertThat(xmlToString(builtDocument)) isEqualTo xml
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldRemovePathsFromExistingXml(fixtureAccessor: FixtureAccessor, namespaceContext: NamespaceContext,
                                       root: Node): Unit = {
    implicit val parser: XmlParser = xmlParser(namespaceContext)
    val xmlProperties = fixtureAccessor.getXmlProperties
    val xml = fixtureAccessor.getPutValueXml
    val oldDocument = XML.loadString(xml)
    val builtDocument = new XmlBuilder(namespaceContext).removeAll(xmlProperties.keySet).build(oldDocument)
    val builtDocumentString = xmlToString(builtDocument)

    println(builtDocumentString)
    xmlProperties.keySet.asScala foreach { xpath =>
      assertThat(for {
        xp  <- xPathCompiler(namespaceContext).compile(xpath).right
        res <- builtDocumentString.evalXPath[kantan.xpath.Node](xp).right
      } yield res).as("Should not evaluate XPath %s", xpath) isEqualTo Left(DecodeError.NotFound)
    }
    assertThat(builtDocumentString) isNotEqualTo fixtureAccessor.getPutValueXml
  }

  private def xmlToString(xml: Node) = {
    val lineSeparator = System.getProperty("line.separator")
    val printer = new PrettyPrinter(80, 4)
    val string = printer.format(xml).replaceAll(s">\n\\s*(\\w.+?)\n\\s*</", ">$1</") + "\n"
    string.replaceAll("\n", lineSeparator)
  }
}
