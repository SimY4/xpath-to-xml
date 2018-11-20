package com.github.simy4.xpath
package scala

import java.util.function.Predicate
import java.util.stream.Stream

import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import javax.xml.namespace.NamespaceContext
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathFactory
import kantan.xpath.{ CompileResult, DecodeError, ParseResult, XPathCompiler, XmlParser }
import org.assertj.core.api.{ Assertions, Condition }
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{ Arguments, ArgumentsProvider, ArgumentsSource }

import _root_.scala.collection.{ Map, mutable }
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
    arguments(new FixtureAccessor("attr"), null, <breakfast_menu/>),
    arguments(new FixtureAccessor("attr"), namespaceContext, <breakfast_menu/>),
    arguments(new FixtureAccessor("special"), null, <records/>),
    arguments(new FixtureAccessor("special"), namespaceContext, <records/>)
  )
}

class XmlBuilderTest {
  import Assertions._
  import XmlBuilderTest._
  import implicits._
  import kantan.xpath.implicits._

  private def xPathCompiler(implicit namespaceContext: NamespaceContext = null): XPathCompiler = namespaceContext match {
    case null => XPathCompiler.builtIn
    case nc   =>
      val xPathFactory = XPathFactory.newInstance
      XPathCompiler { xpathString =>
        CompileResult {
          val xpath = xPathFactory.newXPath
          xpath.setNamespaceContext(nc)
          xpath.compile(xpathString)
        }
      }
  }

  private def xmlParser(implicit namespaceContext: NamespaceContext = null): XmlParser = namespaceContext match {
    case null => XmlParser.builtIn
    case _    =>
      val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
      factory.setNamespaceAware(true)
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
      XmlParser { source => ParseResult(factory.newDocumentBuilder().parse(source)) }
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPaths(fixtureAccessor: FixtureAccessor, namespaceContext: NamespaceContext,
                                         root: Elem): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    implicit val parser: XmlParser = xmlParser
    val xmlProperties = fixtureAccessor.getXmlProperties.asScalaLinkedHashMap
    val builtDocument = root putAll xmlProperties.keys
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.keys foreach { xpath =>
      assertThat((for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[kantan.xpath.Node](xp).right
      } yield res).isRight).isTrue
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(builtDocumentString) is new Condition({ xml: String =>
      fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutXml
    }, "XML matches exactly")
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPathsAndSetValues(fixtureAccessor: FixtureAccessor,
                                                     namespaceContext: NamespaceContext, root: Elem): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    implicit val parser: XmlParser = xmlParser
    val xmlProperties = fixtureAccessor.getXmlProperties.asScalaLinkedHashMap
    val builtDocument = root putAllValues xmlProperties
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties foreach { case (xpath, value) =>
      assertThat(for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[String](xp).right
      } yield res).as("Should evaluate XPath %s to %s", xpath, value) isEqualTo Right(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument)) is new Condition({ xml: String =>
      fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml
    }, "XML matches exactly")
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldModifyDocumentWhenXPathsAreNotTraversable(fixtureAccessor: FixtureAccessor,
                                                      namespaceContext: NamespaceContext, root: Elem): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    implicit val parser: XmlParser = xmlParser
    val xmlProperties = fixtureAccessor.getXmlProperties.asScalaLinkedHashMap
    val xml = fixtureAccessor.getPutXml
    val oldDocument = XML.loadString(xml)
    val builtDocument = oldDocument putAllValues xmlProperties
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties foreach { case (xpath, value) =>
      assertThat(for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[String](xp).right
      } yield res).as("Should evaluate XPath %s to %s", xpath, value) isEqualTo Right(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument)) is new Condition({ xml: String =>
      fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml
    }, "XML matches exactly")
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldNotModifyDocumentWhenAllXPathsTraversable(fixtureAccessor: FixtureAccessor,
                                                      namespaceContext: NamespaceContext, root: Elem): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    implicit val parser: XmlParser = xmlParser
    val xmlProperties = fixtureAccessor.getXmlProperties.asScalaLinkedHashMap
    val xml = fixtureAccessor.getPutValueXml
    val oldDocument = XML.loadString(xml)
    var builtDocument = oldDocument putAllValues xmlProperties
    var builtDocumentString = xmlToString(builtDocument)

    xmlProperties foreach { case (xpath, value) =>
      assertThat(for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[String](xp).right
      } yield res).as("Should evaluate XPath %s to %s", xpath, value) isEqualTo Right(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument)) is new Condition({ xml: String =>
      fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml
    }, "XML matches exactly")

    builtDocument = oldDocument putAll xmlProperties.keys
    builtDocumentString = xmlToString(builtDocument)

    xmlProperties foreach { case (xpath, value) =>
      assertThat(for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[String](xp).right
      } yield res).as("Should evaluate XPath %s to %s", xpath, value) isEqualTo Right(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument)) is new Condition({ xml: String =>
      fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml
    }, "XML matches exactly")
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldRemovePathsFromExistingXml(fixtureAccessor: FixtureAccessor, namespaceContext: NamespaceContext,
                                       root: Elem): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    implicit val parser: XmlParser = xmlParser
    val xmlProperties = fixtureAccessor.getXmlProperties.asScalaLinkedHashMap
    val xml = fixtureAccessor.getPutValueXml
    val oldDocument = XML.loadString(xml)
    val builtDocument = oldDocument removeAll xmlProperties.keys
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.keySet foreach { xpath =>
      assertThat(for {
        xp  <- xPathCompiler.compile(xpath).right
        res <- builtDocumentString.evalXPath[kantan.xpath.Node](xp).right
      } yield res).as("Should not evaluate XPath %s", xpath) isEqualTo Left(DecodeError.NotFound)
    }
    assertThat(builtDocumentString) isNotEqualTo fixtureAccessor.getPutValueXml
  }

  private def xmlToString(xml: Node) = {
    val lineSeparator = System.lineSeparator()
    val printer = new PrettyPrinter(255, 4)
    val string = printer.format(xml).replaceAll(s">\n\\s*(\\w.+?)\n\\s*</", ">$1</") + "\n"
    string.replaceAll("\n", lineSeparator)
  }
}

object XmlBuilderTest {
  private[scala] implicit class JUMapOps[K, V](private val map: java.util.Map[K, V]) extends AnyVal {
    def asScalaLinkedHashMap: Map[K, V] = {
      val linkedHashMap = new mutable.LinkedHashMap[K, V]
      val iterator = map.entrySet.iterator
      while (iterator.hasNext) {
        val next = iterator.next
        linkedHashMap += next.getKey -> next.getValue
      }
      linkedHashMap
    }
  }

  private[scala] implicit def toPredicate[A](p: A => Boolean): Predicate[A] = new Predicate[A] {
    override def test(a: A): Boolean = p(a)
  }
}
