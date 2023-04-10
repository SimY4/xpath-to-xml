module com.github.simych.xpath.core {
  requires transitive java.xml;

  exports com.github.simy4.xpath;
  exports com.github.simy4.xpath.navigator;
  exports com.github.simy4.xpath.spi;
  exports com.github.simy4.xpath.util;

  uses com.github.simy4.xpath.spi.NavigatorSpi;
}