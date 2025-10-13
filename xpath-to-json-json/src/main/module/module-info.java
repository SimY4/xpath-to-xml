module com.github.simych.xpath.json {
  requires transitive com.github.simych.xpath.core;
  requires org.json;

  provides com.github.simy4.xpath.spi.NavigatorSpi with
      com.github.simy4.xpath.json.spi.JsonJsonNavigatorSpi;
}