module com.github.simych.xpath.scala {
  requires transitive com.github.simych.xpath.core;
  requires org.scala.lang.scala3.library;
  requires scala.library;
  requires scala.xml;

  exports com.github.simy4.xpath.scala;
  exports com.github.simy4.xpath.scala.impl;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.scala.spi.ScalaXmlNavigatorSpi;
}