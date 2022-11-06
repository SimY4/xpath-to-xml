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

import collection.{ mutable, Map }
import fixtures.FixtureAccessor
import helpers.SimpleNamespaceContext
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.xml.sax.InputSource
import xml.{ Elem, NamespaceBinding, Node, Null, PrettyPrinter, TopScope, XML }

import javax.xml.namespace.NamespaceContext
import javax.xml.xpath.{ XPathConstants, XPathExpression, XPathFactory }

import java.io.StringReader

@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Null"))
class XmlBuilderSpec extends AnyFunSpec with Matchers {
  val namespaceContext = new SimpleNamespaceContext
  val namespaceBinding = NamespaceBinding("my", namespaceContext.getNamespaceURI("my"), TopScope)

  List(
    (new FixtureAccessor("simple"), <breakfast_menu/>, null),
    (new FixtureAccessor("simple"), <breakfast_menu/>, namespaceContext),
    (
      new FixtureAccessor("ns-simple"),
      Elem("my", "breakfast_menu", Null, namespaceBinding, minimizeEmpty = true),
      namespaceContext
    ),
    (new FixtureAccessor("attr"), <breakfast_menu/>, null),
    (new FixtureAccessor("attr"), <breakfast_menu/>, namespaceContext),
    (new FixtureAccessor("special"), <records/>, null),
    (new FixtureAccessor("special"), <records/>, namespaceContext)
  ).foreach { case (fixtureAccessor, root, namespaceContext) =>
    describe(
      s"with ${fixtureAccessor.toString} and XML ${root.toString} and NS: ${Option(namespaceContext).fold("null")(_.toString)}"
    ) {
      implicit val ns: NamespaceContext = namespaceContext

      describe("should build document from set of XPaths: ") {
        val xmlProperties = fixtureAccessor.getXmlProperties.asScala
        val builtDocument = xmlProperties.keys
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
            for {
              xs  <- acc
              eff <- Effect.put(xpath)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(root))
          .getOrThrow
        val builtDocumentString = xmlToString(builtDocument)

        xmlProperties.keys.foreach { xpath =>
          val documentSource: InputSource = builtDocumentString
          it(s"$xpath should evaluate")(xpath.evaluate(documentSource, XPathConstants.NODE) shouldNot be(null))
        }
        // although these cases are working fine the order of attribute is messed up
        if (!fixtureAccessor.toString.startsWith("attr")) {
          it("should match exactly")(builtDocumentString should ===(fixtureAccessor.getPutXml))
        }
      }

      describe("should build document from set of XPaths and set values") {
        val xmlProperties = fixtureAccessor.getXmlProperties.asScala
        val builtDocument = xmlProperties.toSeq
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
            for {
              xs  <- acc
              eff <- Effect.putValue(pair._1, pair._2)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(root))
          .getOrThrow
        val builtDocumentString = xmlToString(builtDocument)

        xmlProperties.foreach { case (xpath, value) =>
          val documentSource: InputSource = builtDocumentString
          it(s"$xpath should evaluate to ${value.toString}") {
            xpath.evaluate(documentSource, XPathConstants.STRING) should equal(value)
          }
        }
        // although these cases are working fine the order of attribute is messed up
        if (!fixtureAccessor.toString.startsWith("attr")) {
          it("should match exactly")(builtDocumentString should ===(fixtureAccessor.getPutValueXml))
        }
      }

      describe("should modify document when XPaths are not traversable") {
        val xmlProperties = fixtureAccessor.getXmlProperties.asScala
        val xml           = fixtureAccessor.getPutXml
        val oldDocument   = XML.loadString(xml)
        val builtDocument = xmlProperties.toSeq
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
            for {
              xs  <- acc
              eff <- Effect.putValue(pair._1, pair._2)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(oldDocument))
          .getOrThrow
        val builtDocumentString = xmlToString(builtDocument)

        xmlProperties.foreach { case (xpath, value) =>
          val documentSource: InputSource = builtDocumentString
          it(s"$xpath should evaluate to ${value.toString}") {
            xpath.evaluate(documentSource, XPathConstants.STRING) should equal(value)
          }
        }
        // although these cases are working fine the order of attribute is messed up
        if (!fixtureAccessor.toString.startsWith("attr")) {
          it("should match exactly")(builtDocumentString should ===(fixtureAccessor.getPutValueXml))
        }
      }

      describe("should not modify document when XPaths are traversable") {
        val xmlProperties = fixtureAccessor.getXmlProperties.asScala
        val xml           = fixtureAccessor.getPutValueXml
        val oldDocument   = XML.loadString(xml)
        val builtDocument1 = xmlProperties.toSeq
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (pair, acc) =>
            for {
              xs  <- acc
              eff <- Effect.putValue(pair._1, pair._2)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(oldDocument))
          .getOrThrow
        val builtDocumentString1 = xmlToString(builtDocument1)

        xmlProperties.foreach { case (xpath, value) =>
          val documentSource: InputSource = builtDocumentString1
          it(s"first: $xpath should evaluate to ${value.toString}") {
            xpath.evaluate(documentSource, XPathConstants.STRING) should equal(value)
          }
        }
        // although these cases are working fine the order of attribute is messed up
        if (!fixtureAccessor.toString.startsWith("attr")) {
          it("first: should match exactly")(builtDocumentString1 should ===(fixtureAccessor.getPutValueXml))
        }

        val builtDocument2 = xmlProperties.keys
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
            for {
              xs  <- acc
              eff <- Effect.put(xpath)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(oldDocument))
          .getOrThrow
        val builtDocumentString2 = xmlToString(builtDocument2)

        xmlProperties.foreach { case (xpath, value) =>
          val documentSource: InputSource = builtDocumentString2
          it(s"second: $xpath should evaluate to ${value.toString}") {
            xpath.evaluate(documentSource, XPathConstants.STRING) should equal(value)
          }
        }
        // although these cases are working fine the order of attribute is messed up
        if (!fixtureAccessor.toString.startsWith("attr")) {
          it("second: should match exactly")(builtDocumentString2 should ===(fixtureAccessor.getPutValueXml))
        }
      }

      describe("should remove XPaths from existing XML") {
        val xmlProperties = fixtureAccessor.getXmlProperties.asScala
        val xml           = fixtureAccessor.getPutValueXml
        val oldDocument   = XML.loadString(xml)
        val builtDocument = xmlProperties.keys
          .foldRight(Right(Nil): Either[Throwable, List[Effect]]) { (xpath, acc) =>
            for {
              xs  <- acc
              eff <- Effect.remove(xpath)
            } yield eff :: xs
          }
          .flatMap(XmlBuilder(_)(oldDocument))
          .getOrThrow
        val builtDocumentString = xmlToString(builtDocument)

        xmlProperties.keys.foreach { xpath =>
          val documentSource: InputSource = builtDocumentString
          it(s"$xpath should not evaluate")(xpath.evaluate(documentSource, XPathConstants.NODE) shouldBe null)
        }
        it("second: should not match")(builtDocumentString shouldNot equal(fixtureAccessor.getPutValueXml))
      }
    }
  }

  private def xmlToString(xml: Node) = {
    val lineSeparator = System.lineSeparator()
    val printer       = new PrettyPrinter(255, 4)
    val string        = printer.format(xml).replaceAll(s">\n\\s*(\\w.+?)\n\\s*</", ">$1</") + "\n"
    string.replaceAll("\n", lineSeparator)
  }

  implicit def toXPathExpression(xpathString: String)(implicit nc: NamespaceContext): XPathExpression = {
    val xpath = XPathFactory.newInstance().newXPath()
    Option(nc).foreach(xpath.setNamespaceContext)
    xpath.compile(xpathString)
  }

  implicit def toInputSource(xmlString: String): InputSource = new InputSource(new StringReader(xmlString))

  implicit class JUMapOps[K, V](map: java.util.Map[K, V]) {
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

  implicit class EitherOps[L, R](either: Either[L, R]) {
    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    def getOrThrow(implicit ev: L <:< Throwable): R = either.fold(throw _, identity)
  }
}
