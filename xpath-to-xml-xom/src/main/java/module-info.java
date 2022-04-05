module com.github.simych.xpath.xom {
  requires transitive com.github.simych.xpath.core;
  requires nu.xom;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.xom.spi.XomNavigatorSpi;
}
