module com.github.simych.xpath.jdom {
  requires transitive com.github.simych.xpath.core;
  requires org.jdom2;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.jdom.spi.JDomNavigatorSpi;
}