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
package scala

import java.io.StringReader
import java.util.stream.Stream

import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import javax.xml.namespace.NamespaceContext
import javax.xml.xpath.{ XPathConstants, XPathExpression, XPathFactory }
import org.assertj.core.api.{ Assertions, Condition }
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.{ Arguments, ArgumentsProvider, ArgumentsSource }
import org.xml.sax.InputSource

import collection.{ mutable, Map }
import xml.{ Elem, NamespaceBinding, Node, Null, PrettyPrinter, TopScope, XML }

class DataProvider extends ArgumentsProvider {

  private val namespaceContext = new SimpleNamespaceContext
  private val namespaceBinding = NamespaceBinding("my", namespaceContext.getNamespaceURI("my"), TopScope)

  override def provideArguments(context: ExtensionContext): Stream[_ <: Arguments] =
    java.util.Arrays.stream(
      Array[Arguments](
        (new FixtureAccessor("simple"), null, <breakfast_menu/>),
        (new FixtureAccessor("simple"), namespaceContext, <breakfast_menu/>),
        (
          new FixtureAccessor("ns-simple"),
          namespaceContext,
          Elem("my", "breakfast_menu", Null, namespaceBinding, minimizeEmpty = true)
        ),
        (new FixtureAccessor("attr"), null, <breakfast_menu/>),
        (new FixtureAccessor("attr"), namespaceContext, <breakfast_menu/>),
        (new FixtureAccessor("special"), null, <records/>),
        (new FixtureAccessor("special"), namespaceContext, <records/>)
      )
    )

  implicit private def arguments(p: Product): Arguments =
    new Arguments {
      @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.AsInstanceOf"))
      override def get(): Array[AnyRef] = p.productIterator.toArray.asInstanceOf[Array[AnyRef]]
    }
}

class XmlBuilderTest {
  import Assertions._
  import XmlBuilderTest._

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPaths(
    fixtureAccessor: FixtureAccessor,
    namespaceContext: NamespaceContext,
    root: Elem
  ): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    val xmlProperties                 = fixtureAccessor.getXmlProperties.asScala
    val builtDocument = xmlProperties.keys
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
        acc >>= { xs =>
          Effect.put(xpath).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(root))
      .unsafeGet
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.keys.foreach { xpath =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.NODE)).isNotNull
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(builtDocumentString).is(
      new Condition(
        (xml: String) => fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutXml,
        "\"%s\" matches exactly",
        fixtureAccessor.getPutXml
      )
    )
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldBuildDocumentFromSetOfXPathsAndSetValues(
    fixtureAccessor: FixtureAccessor,
    namespaceContext: NamespaceContext,
    root: Elem
  ): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    val xmlProperties                 = fixtureAccessor.getXmlProperties.asScala
    val builtDocument = xmlProperties.toSeq
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
        acc >>= { xs =>
          Effect.putValue(pair._1, pair._2).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(root))
      .unsafeGet
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.foreach { case (xpath, value) =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.STRING))
        .as("Should evaluate XPath %s to %s", xpath, value)
        .isEqualTo(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(xmlToString(builtDocument)).is(
      new Condition(
        (xml: String) => fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml,
        "\"%s\" matches exactly",
        fixtureAccessor.getPutValueXml
      )
    )
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldModifyDocumentWhenXPathsAreNotTraversable(
    fixtureAccessor: FixtureAccessor,
    namespaceContext: NamespaceContext,
    root: Elem
  ): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    val xmlProperties                 = fixtureAccessor.getXmlProperties.asScala
    val xml                           = fixtureAccessor.getPutXml
    val oldDocument                   = XML.loadString(xml)
    val builtDocument = xmlProperties.toSeq
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
        acc >>= { xs =>
          Effect.putValue(pair._1, pair._2).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(oldDocument))
      .unsafeGet
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.foreach { case (xpath, value) =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.STRING))
        .as("Should evaluate XPath %s to %s", xpath, value)
        .isEqualTo(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(builtDocumentString).is(
      new Condition(
        (xml: String) => fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml,
        "\"%s\" matches exactly",
        fixtureAccessor.getPutValueXml
      )
    )
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldNotModifyDocumentWhenAllXPathsTraversable(
    fixtureAccessor: FixtureAccessor,
    namespaceContext: NamespaceContext,
    root: Elem
  ): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    val xmlProperties                 = fixtureAccessor.getXmlProperties.asScala
    val xml                           = fixtureAccessor.getPutValueXml
    val oldDocument                   = XML.loadString(xml)
    var builtDocument = xmlProperties.toSeq
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
        acc >>= { xs =>
          Effect.putValue(pair._1, pair._2).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(oldDocument))
      .unsafeGet
    var builtDocumentString = xmlToString(builtDocument)

    xmlProperties.foreach { case (xpath, value) =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.STRING))
        .as("Should evaluate XPath %s to %s", xpath, value)
        .isEqualTo(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(builtDocumentString).is(
      new Condition(
        (xml: String) => fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml,
        "\"%s\" matches exactly",
        fixtureAccessor.getPutValueXml
      )
    )

    builtDocument = xmlProperties.keys
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
        acc >>= { xs =>
          Effect.put(xpath).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(oldDocument))
      .unsafeGet
    builtDocumentString = xmlToString(builtDocument)

    xmlProperties.foreach { case (xpath, value) =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.STRING))
        .as("Should evaluate XPath %s to %s", xpath, value)
        .isEqualTo(value)
    }
    // although these cases are working fine the order of attribute is messed up
    assertThat(builtDocumentString).is(
      new Condition(
        (xml: String) => fixtureAccessor.toString.startsWith("attr") || xml == fixtureAccessor.getPutValueXml,
        "\"%s\" matches exactly",
        fixtureAccessor.getPutValueXml
      )
    )
  }

  @ParameterizedTest
  @ArgumentsSource(classOf[DataProvider])
  def shouldRemovePathsFromExistingXml(
    fixtureAccessor: FixtureAccessor,
    namespaceContext: NamespaceContext,
    root: Elem
  ): Unit = {
    implicit val ns: NamespaceContext = namespaceContext
    val xmlProperties                 = fixtureAccessor.getXmlProperties.asScala
    val xml                           = fixtureAccessor.getPutValueXml
    val oldDocument                   = XML.loadString(xml)
    val builtDocument = xmlProperties.keys
      .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
        acc >>= { xs =>
          Effect.remove(xpath).fmap(_ :: xs)
        }
      }
      .>>=(XmlBuilder(_)(oldDocument))
      .unsafeGet
    val builtDocumentString = xmlToString(builtDocument)

    xmlProperties.keySet.foreach { xpath =>
      val documentSource: InputSource = builtDocumentString
      assertThat(xpath.evaluate(documentSource, XPathConstants.NODE))
        .as("Should not evaluate XPath %s", xpath)
        .isNull()
    }
    assertThat(builtDocumentString).isNotEqualTo(fixtureAccessor.getPutValueXml)
  }

  private def xmlToString(xml: Node) = {
    val lineSeparator = System.lineSeparator()
    val printer       = new PrettyPrinter(255, 4)
    val string        = printer.format(xml).replaceAll(s">\n\\s*(\\w.+?)\n\\s*</", ">$1</") + "\n"
    string.replaceAll("\n", lineSeparator)
  }

  implicit private def toXPathExpression(xpathString: String)(implicit nc: NamespaceContext): XPathExpression = {
    val xpath = XPathFactory.newInstance().newXPath()
    Option(nc).foreach(xpath.setNamespaceContext)
    xpath.compile(xpathString)
  }

  implicit private def toInputSource(xmlString: String): InputSource = new InputSource(new StringReader(xmlString))
}

//noinspection ConvertExpressionToSAM
object XmlBuilderTest {
  implicit private[scala] class JUMapOps[K, V](private val map: java.util.Map[K, V]) extends AnyVal {
    def asScala: Map[K, V] = {
      val linkedHashMap = new mutable.LinkedHashMap[K, V]
      val iterator      = map.entrySet().iterator()
      while (iterator.hasNext) {
        val entry = iterator.next()
        linkedHashMap += entry.getKey -> entry.getValue
      }
      linkedHashMap
    }
  }

  implicit private[scala] class EitherOps[+L, +R](private val either: Either[L, R]) extends AnyVal {
    def fmap[RR](f: R => RR): Either[L, RR] =
      >>= { r =>
        Right(f(r))
      }
    def >>=[LL >: L, RR](f: R => Either[LL, RR]): Either[LL, RR] = either.fold(Left(_), f)
    def unsafeGet(implicit ev: L <:< Throwable): R               = either.fold(ex => throw ev(ex), identity)
  }
  implicit private[scala] def asJavaPredicate[A](p: A => Boolean): java.util.function.Predicate[A] =
    new java.util.function.Predicate[A] {
      override def test(a: A): Boolean = p(a)
    }
}
