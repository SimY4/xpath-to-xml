module com.github.simych.xpath.dom4j {
  requires transitive com.github.simych.xpath.core;
  requires dom4j;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.dom4j.spi.Dom4jNavigatorSpi;
}
