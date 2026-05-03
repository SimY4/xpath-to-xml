module com.github.simych.xpath.dom {
  requires transitive com.github.simych.xpath.core;
  requires java.xml;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.dom.spi.DomNavigatorSpi;
}